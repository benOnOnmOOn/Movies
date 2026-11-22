# Android Architecture Guide

> Our architecture rules, design patterns and the thinking behind them.
> Everyone who works on the codebase should read it. Update it when we change a decision.

**Stack:** Kotlin · Jetpack Compose · Coroutines/Flow · kotlinx.serialization · Hilt¹ · Room · Retrofit + OkHttp · DataStore · Navigation Compose · Firebase (Crashlytics)
**Based on:** [Google's official Guide to App Architecture](https://developer.android.com/topic/architecture) and the [Now in Android](https://github.com/android/nowinandroid) reference app.

¹ Hilt (`com.google.dagger`) is outside the dependency allowlist but approved as an exception. See [§7.3](#73-approved-exceptions).

---

## 1. Philosophy

These principles come first. Every rule below follows from one of them.

| # | Principle | What it means in practice |
|---|-----------|---------------------------|
| 1 | **Separation of concerns** | Activities, Fragments and Composables hold only UI logic. They never call the network or the database directly. |
| 2 | **Drive UI from data models** | The UI shows state. It does not own state. State lives in ViewModels and the data layer, so it survives configuration changes and process death. |
| 3 | **Single source of truth (SSOT)** | Each type of data has one owner, usually a repository backed by the local DB. Only the owner can change it. Everyone else reads it. |
| 4 | **Unidirectional data flow (UDF)** | State goes **down** (data → UI). Events go **up** (UI → data). Data never flows both ways. |
| 5 | **Offline-first** | The UI reads from local storage. The network only updates local storage. |
| 6 | **Depend on abstractions** | Layers talk to each other through interfaces, and the DI container wires them together. Implementations can be swapped and faked in tests. |
| 7 | **Immutability by default** | UI state, domain models and DTOs are `data class`es with `val`s. Expose `StateFlow`/`Flow`, never `MutableStateFlow`. |
| 8 | **Testability is a design requirement** | If something is hard to test, the design is wrong. Fix the design, not the test. Use fakes, not mocks ([§11.2](#112-fakes-over-mocks--strict-mocking-rules)). |
| 9 | **Minimal dependencies** | Only `androidx`, `kotlinx`, Retrofit and Square libraries are allowed. Anything else needs an ADR ([§7](#7-dependency-policy)). |
| 10 | **Compile-time over runtime** | No reflection. Errors should fail the build, not crash on a user's phone ([§8](#8-no-reflection)). |
| 11 | **Less code, not more** | Use Kotlin language features instead of boilerplate. Add a layer or abstraction only when it solves a real problem ([§9](#9-no-boilerplate)). |
| 12 | **Multiplatform-ready** | Write non-UI code in pure Kotlin with no `java.*` or `android.*` imports, so it could move to Kotlin Multiplatform later ([§10](#10-kotlin-multiplatform-friendly-code)). |
| 13 | **Fail loudly, never swallow** | Data and domain code throws exceptions. The ViewModel handles them in one `CoroutineExceptionHandler` that logs every unexpected error and reports it to Crashlytics as a non-fatal ([§6.3](#63-error-handling)). |

---

## 2. Layered architecture

```
┌─────────────────────────────────────────────┐
│                 UI LAYER                    │
│   Composables (UI elements) ◄── UiState     │
│          │ events                ▲          │
│          ▼                       │          │
│        ViewModel (state holder) ─┘          │
└───────────────────┬─────────────────────────┘
                    │ calls / collects Flow
┌───────────────────▼─────────────────────────┐
│         DOMAIN LAYER (optional)             │
│  Use cases: reusable / complex business     │
│  logic that combines several repositories   │
└───────────────────┬─────────────────────────┘
                    │
┌───────────────────▼─────────────────────────┐
│                DATA LAYER                   │
│  Repositories (SSOT, public API)            │
│     ├── LocalDataSource  (Room, DataStore)  │
│     └── RemoteDataSource (Retrofit)         │
└─────────────────────────────────────────────┘
```

**Dependency rule:** a layer may depend only on the layer **below** it. The data layer knows nothing about the UI. The domain layer has no Android framework dependencies.

### 2.1 UI layer

**Parts:** Composable screens, `ViewModel`s, `UiState` models.

Rules:
- Each screen has **one ViewModel** and exposes **one `StateFlow<UiState>`**.
- Model `UiState` as a `sealed interface` (Loading / Success / Error) or as one data class with flags. Pick whichever fits the screen best.
- Collect state with `collectAsStateWithLifecycle()`.
- Split each screen into a **stateful `Route`** composable (gets the ViewModel) and a **stateless `Screen`** composable (takes state and lambdas). Previews and UI tests use the stateless one.
- The ViewModel must **not** reference `Context`, `View`, `Activity`, `NavController` or any Compose types.
- The ViewModel handles errors in **one `CoroutineExceptionHandler`** and launches every coroutine through it. The handler logs the error, reports it to Crashlytics and puts it in the UI state ([§6.3](#63-error-handling)).
- One-off events (navigation, snackbar) are **modelled as state** that the UI consumes. Do not send them through a `Channel`/`SharedFlow`. [See why](https://medium.com/androiddevelopers/viewmodel-one-off-event-antipatterns-16a1da869b95), and see [§5.8](#58-navigating-after-an-action).

```kotlin
// UiState
data class ArticlesUiState(
    val isLoading: Boolean = true,
    val articles: List<ArticleUi> = emptyList(),
    val error: UiText? = null,
)

// ViewModel
@HiltViewModel
class ArticlesViewModel @Inject constructor(
    private val getArticles: GetArticlesUseCase,
    private val bookmarkArticle: BookmarkArticleUseCase,
    private val errorReporter: ErrorReporter,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ArticlesUiState())
    val uiState: StateFlow<ArticlesUiState> = _uiState.asStateFlow()

    // General error handling for every coroutine in this ViewModel (§6.3)
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        errorReporter.report(throwable)                    // Logcat + Crashlytics non-fatal
        _uiState.update { it.copy(isLoading = false, error = throwable.toUiText()) }
    }
    private val scope = viewModelScope + exceptionHandler

    private var loadJob: Job? = null

    init { loadArticles() }

    fun onBookmarkClick(id: ArticleId) {
        scope.launch { bookmarkArticle(id) }               // no try/catch: errors go to the handler
    }

    fun onRetryClick() = loadArticles()

    private fun loadArticles() {
        loadJob?.cancel()
        _uiState.update { it.copy(isLoading = true, error = null) }
        loadJob = scope.launch {
            getArticles().collect { articles ->
                _uiState.update { it.copy(isLoading = false, articles = articles.map(Article::toUi)) }
            }
        }
    }
}

// Stateful route
@Composable
fun ArticlesRoute(
    onArticleClick: (ArticleId) -> Unit,
    viewModel: ArticlesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ArticlesScreen(
        uiState = uiState,
        onArticleClick = onArticleClick,
        onBookmarkClick = viewModel::onBookmarkClick,
        onRetryClick = viewModel::onRetryClick,
    )
}

// Stateless screen: previewable and testable
@Composable
fun ArticlesScreen(
    uiState: ArticlesUiState,
    onArticleClick: (ArticleId) -> Unit,
    onBookmarkClick: (ArticleId) -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        uiState.isLoading -> LoadingIndicator(modifier)
        uiState.error != null -> ErrorView(uiState.error, onRetryClick, modifier)
        else -> ArticleList(uiState.articles, onArticleClick, onBookmarkClick, modifier)
    }
}
```

### 2.2 Domain layer (optional)

**Add a use case when:**
- several ViewModels share the same business logic, or
- the logic combines data from **multiple repositories**, or
- the logic is complex enough that it deserves its own unit tests.

**Don't** add pass-through use cases that only call `repository.getX()`. In that case the ViewModel calls the repository directly ([§9](#9-no-boilerplate)).

Rules:
- Use cases are named `VerbNounUseCase` (e.g. `GetUserFeedUseCase`).
- Each use case has **one public `operator fun invoke`**.
- Pure Kotlin only, with no `android.*` or `java.*` imports ([§10](#10-kotlin-multiplatform-friendly-code)).
- Main-safe: a use case moves blocking work onto an injected dispatcher.
- Return plain values or `Flow` and **throw** on failure. Don't return `Result` ([§6.3](#63-error-handling)).

```kotlin
class GetUserFeedUseCase @Inject constructor(
    private val articlesRepository: ArticlesRepository,
    private val userDataRepository: UserDataRepository,
) {
    operator fun invoke(): Flow<List<UserArticle>> =
        combine(
            articlesRepository.observeArticles(),
            userDataRepository.userData,
        ) { articles, userData ->
            articles
                .filter { it.topicId in userData.followedTopics }
                .map { UserArticle(it, isBookmarked = it.id in userData.bookmarks) }
        }
}
```

### 2.3 Data layer

**Parts:** Repositories (public), data sources (internal), DTOs/entities, mappers.

Rules:
- A **repository** is the only entry point to its data. It is exposed as an `interface`, with an `Offline…`/`Default…` implementation.
- Repositories expose **`Flow`** for observable data and **`suspend fun`** for one-shot operations. They return plain values and **throw** on failure. They don't return `Result` ([§6.3](#63-error-handling)).
- Data sources translate low-level exceptions (`IOException`, `HttpException`, `SQLiteException`) into domain exceptions (`AppException`), always keeping the original as `cause`.
- Data sources deal with **one** source each (one API, one DAO, one DataStore). They are `internal` to the data module.
- DTOs and entities never leave the data layer. Map them to domain models at the repository boundary: `ArticleDto` (network) → `ArticleEntity` (DB) → `Article` (domain). Add an `ArticleUi` model only when the UI needs a different shape. Mappers are extension functions next to the models: `fun ArticleDto.toEntity()`.
- Serialize with **kotlinx.serialization** (`@Serializable`), never with reflection-based libraries ([§8](#8-no-reflection)).
- All repository functions are **main-safe**.
- **One table per entity, not one per list.** Don't copy the same entity into `PopularMovieEntity` and `PlayingNowMovieEntity`. Store each movie once, and model lists with a category column or a cross-reference table (`MovieListEntry(listType, movieId, position)`).
- **Refresh is atomic.** Fetch first, then replace the stored data in one Room `@Transaction`. Never clear local data before the new data has arrived: a failed refresh must leave the old data on screen.

```kotlin
@Dao
internal abstract class MovieListDao {
    @Upsert abstract suspend fun upsertMovies(movies: List<MovieEntity>)
    @Insert abstract suspend fun insertEntries(entries: List<MovieListEntry>)
    @Query("DELETE FROM movie_list_entry WHERE listType = :type") abstract suspend fun deleteList(type: ListType)

    @Transaction
    open suspend fun replaceList(type: ListType, movies: List<MovieEntity>) {
        deleteList(type)
        upsertMovies(movies)
        insertEntries(movies.mapIndexed { index, movie -> MovieListEntry(type, movie.id, position = index) })
    }
}

// Repository
override suspend fun refresh(type: ListType) {
    val movies = remote.getMovies(type)                     // 1. fetch: throws on failure, old data untouched
    dao.replaceList(type, movies.map(MovieDto::toEntity))   // 2. swap in one transaction
}
```

```kotlin
interface ArticlesRepository {
    fun observeArticles(): Flow<List<Article>>
    suspend fun sync()   // throws AppException on failure
}

internal class OfflineFirstArticlesRepository @Inject constructor(
    private val dao: ArticleDao,
    private val remote: ArticlesRemoteDataSource,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : ArticlesRepository {

    // SSOT: the UI always reads from the database
    override fun observeArticles(): Flow<List<Article>> =
        dao.observeAll().map { entities -> entities.map(ArticleEntity::toDomain) }

    // The network only writes into the database. Failures propagate to the caller.
    override suspend fun sync() {
        withContext(ioDispatcher) {
            dao.upsertAll(remote.getArticles().map(ArticleDto::toEntity))
        }
    }
}
```

---

## 3. Modularization

We split **by feature** and then **by layer**, following the Now in Android structure.

```
:app                      → Application, MainActivity, AppNavHost, top-level navigation, DI graph root
:feature:articles         → UI + ViewModel + navigation for one feature (depends on :core:*)
:feature:settings
:core:domain              → Use cases (optional) — pure Kotlin
:core:data                → Repositories (public interfaces + impl)
:core:database            → Room DB, DAOs, entities
:core:network             → Retrofit API, DTOs
:core:datastore           → Preferences / Proto DataStore
:core:model               → Pure Kotlin domain models + AppException hierarchy (no Android or Java deps)
:core:designsystem        → Theme, colors, typography, reusable components
:core:ui                  → Shared composables that use domain models
:core:common              → Dispatchers, platform abstractions (Clock, StringResolver, ErrorReporter)
:core:logging             → ErrorReporter implementation (Logcat + Firebase Crashlytics)
:core:testing             → Fakes, test rules, test dispatchers
build-logic/convention    → Gradle convention plugins
```

Rules:
- `:feature:*` modules **never depend on other feature modules**. Navigation between features goes through lambdas that `:app` wires together ([§5](#5-navigation)).
- `:core:model` and `:core:domain` are pure Kotlin/JVM modules today, ready to become `commonMain` later ([§10](#10-kotlin-multiplatform-friendly-code)).
- Use the **`internal`** visibility modifier aggressively. A module exposes only its public API.
- **Module path, package and namespace match.** Use one root package (`com.example.movies`). Each module's package and Android `namespace` is the root plus the module path: `:core:network` → `com.example.movies.core.network`, `:feature:popular` → `com.example.movies.feature.popular`. Set the namespace in the convention plugin so it can't drift.
- Code never lives in another module's package. Check module names for typos before the first commit, because renaming a module later is expensive.
- Share build configuration through **convention plugins** in `build-logic/` and versions through **`gradle/libs.versions.toml`**. Do not copy-paste `build.gradle.kts` blocks.

> Small apps can start as a single `:app` module that uses the same **packages** (`ui/`, `domain/`, `data/`). Split it into modules when build times or team size call for it.

---

## 4. Design patterns

| Pattern | Where | Why |
|---------|-------|-----|
| **MVVM + UDF** (state-driven, MVI-flavoured) | UI layer | Lifecycle-aware, testable, and a natural fit for Compose |
| **Repository** | Data layer | One source of truth that hides where data comes from |
| **Use case / Interactor** | Domain layer | Reusable, focused business rules |
| **Dependency Injection** (constructor injection, Hilt) | Everywhere | Loose coupling and easy fakes in tests |
| **Observer** (Kotlin `Flow`/`StateFlow`) | Data → UI | Reactive updates with no manual refreshes |
| **Mapper** (extension functions) | Layer boundaries | Keeps models separate so API changes don't leak into the UI |
| **Strategy** (interface + multiple impls) | e.g. `NetworkMonitor`, `Clock`, `ErrorReporter`, `AnalyticsHelper` | Swap implementations per build type, platform or test |
| **Assisted Inject** | ViewModels with runtime args | Inject runtime parameters cleanly |
| **Sealed hierarchies** | `UiState`, domain exceptions (`AppException`) | Exhaustive `when` lets the compiler check every case |
| **State hoisting** | Compose | Stateless, reusable, previewable composables |
| **Delegation** (`by`) | Shared behaviour | Composition instead of base classes |
| **Facade** | `:core:designsystem` components | One place to change the look and feel |

### Patterns we avoid

- ❌ **God ViewModels / BaseViewModel / BaseActivity inheritance chains.** Use composition, delegation and small helpers instead.
- ❌ **Service locators / static singletons** (`object Repo`). Use DI with `@Singleton` instead.
- ❌ **`LiveData` in new code.** Use `StateFlow` instead.
- ❌ **`GlobalScope`.** Use `viewModelScope` or an injected `@ApplicationScope CoroutineScope` instead.
- ❌ **Event buses.** Use explicit flows and callbacks instead.
- ❌ **Passing `Context` or `NavController` into ViewModels or repositories.** Inject an abstraction (e.g. `StringResolver`) or use `@ApplicationContext` only in the data layer.
- ❌ **Leaking DTOs or entities into the UI.**
- ❌ **Swallowing exceptions**, or returning `Result` by default ([§6.3](#63-error-handling)).
- ❌ **Reflection of any kind** ([§8](#8-no-reflection)).
- ❌ **Mocking libraries for anything other than `Context` and time** ([§11.2](#112-fakes-over-mocks--strict-mocking-rules)).

---

## 5. Navigation

We use **Navigation Compose with type-safe routes** (`androidx.navigation`). Routes are `@Serializable` classes, so arguments are checked at compile time. The kotlinx.serialization compiler plugin generates the code, with no reflection.

### 5.1 Principles

1. **Navigation belongs to the UI layer.** Only composables in `:app` and the feature `…Navigation.kt` files touch `NavController`.
2. **ViewModels never hold a `NavController`** and never emit "navigate" commands. They expose state, and the UI decides to navigate ([§5.8](#58-navigating-after-an-action)).
3. **Features don't know about each other.** A feature exposes navigation callbacks (`onArticleClick: (ArticleId) -> Unit`), and `:app` connects them to the right destination.
4. **Pass IDs, not objects.** Pass the smallest possible argument, usually a typed ID. The destination loads the data from its repository, which is the single source of truth. Never pass whole models, lists or `Parcelable`s.
5. **Arguments must survive process death.** Routes are saved in the back stack, so keep them small and serializable.
6. **The back stack is the history the user expects.** The system back button must always work. Don't fight it with custom back logic.

### 5.2 Defining destinations

Each feature has a `navigation/XxxNavigation.kt` file with three parts:

```kotlin
// feature/articles/navigation/ArticlesNavigation.kt

// 1. Destinations: type-safe, serializable, internal where possible
@Serializable data object ArticlesGraph                                 // nested graph
@Serializable internal data object ArticlesDestination                  // list screen
@Serializable data class ArticleDetailDestination(val articleId: String) // detail screen

// 2. Public navigation extension(s) on NavController
fun NavController.navigateToArticleDetail(articleId: ArticleId, navOptions: NavOptions? = null) =
    navigate(ArticleDetailDestination(articleId.value), navOptions)

// 3. Public NavGraphBuilder extension: the feature's entry point
fun NavGraphBuilder.articlesSection(
    onArticleClick: (ArticleId) -> Unit,
    onBackClick: () -> Unit,
) {
    navigation<ArticlesGraph>(startDestination = ArticlesDestination) {
        composable<ArticlesDestination> {
            ArticlesRoute(onArticleClick = onArticleClick)
        }
        composable<ArticleDetailDestination>(
            deepLinks = listOf(
                navDeepLink<ArticleDetailDestination>(basePath = "https://example.com/article"),
            ),
        ) {
            ArticleDetailRoute(onBackClick = onBackClick)
        }
    }
}
```

Naming:
- `XxxDestination` is the serializable navigation key.
- `XxxRoute` is the stateful composable and `XxxScreen` the stateless one ([§2.1](#21-ui-layer)).
- `XxxGraph` is a nested graph.

### 5.3 The app NavHost

`:app` is the only place that knows all the features and wires them together:

```kotlin
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = ArticlesGraph,
        modifier = modifier,
    ) {
        articlesSection(
            onArticleClick = { id -> navController.navigateToArticleDetail(id) },
            onBackClick = { navController.popBackStack() },
        )
        settingsScreen(
            onBackClick = { navController.popBackStack() },
        )
    }
}
```

### 5.4 Reading arguments

Read arguments in the **ViewModel** through `SavedStateHandle`, not in the composable. They survive process death this way and the ViewModel stays testable.

```kotlin
@HiltViewModel
class ArticleDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    articlesRepository: ArticlesRepository,
    errorReporter: ErrorReporter,
) : ViewModel() {

    private val articleId = ArticleId(savedStateHandle.toRoute<ArticleDetailDestination>().articleId)

    private val _uiState = MutableStateFlow(ArticleDetailUiState())
    val uiState: StateFlow<ArticleDetailUiState> = _uiState.asStateFlow()

    private val scope = viewModelScope + CoroutineExceptionHandler { _, throwable ->
        errorReporter.report(throwable)
        _uiState.update { it.copy(isLoading = false, error = throwable.toUiText()) }
    }

    init {
        scope.launch {
            articlesRepository.observeArticle(articleId).collect { article ->
                _uiState.update { it.copy(isLoading = false, article = article) }
            }
        }
    }
}
```

In tests, build the handle directly: `SavedStateHandle(mapOf("articleId" to "42"))`. No mocking needed.

### 5.5 Top-level destinations (bottom bar / navigation rail)

- Define them in one `enum class TopLevelDestination` in `:app`.
- Each tab keeps **its own back stack**. Save and restore it when switching tabs:

```kotlin
enum class TopLevelDestination(
    val graph: Any,
    @StringRes val label: Int,
    val icon: ImageVector,
) {
    ARTICLES(ArticlesGraph, R.string.articles, AppIcons.Articles),
    SETTINGS(SettingsGraph, R.string.settings, AppIcons.Settings),
}

fun NavController.navigateToTopLevel(destination: TopLevelDestination) {
    navigate(destination.graph) {
        popUpTo(graph.findStartDestination().id) { saveState = true } // don't pile up tabs
        launchSingleTop = true                                         // no duplicate on re-select
        restoreState = true                                            // restore the tab's back stack
    }
}
```

- Highlight the selected tab from `currentBackStackEntryAsState()` with `destination.hierarchy.any { it.hasRoute(graph::class) }`.
- Use `NavigationSuiteScaffold` (`androidx.compose.material3.adaptive`) so the bar becomes a rail on large screens automatically.

### 5.6 Deep links

- Declare deep links on the destination with `navDeepLink<T>(basePath = …)` and add matching `<intent-filter>`s in the manifest.
- For https links, verify **App Links** (`assetlinks.json`).
- **Treat deep-link arguments as untrusted input.** Validate them in the ViewModel and show an error state if the ID is unknown. Never crash.
- When the user opens a deep link from outside the app, pressing back must go to a sensible parent (for example the list), not exit the app.

### 5.7 Returning results to a previous screen

Prefer these options, in this order:
1. **Shared state through the data layer.** The second screen writes to a repository, and the first screen observes it. This works across process death and needs no extra navigation code.
2. **Hoisted local state** for pickers and dialogs that live inside the same screen.
3. **`previousBackStackEntry?.savedStateHandle`** for small, simple results only. Define the key as a `const val` in the feature that owns the result.

### 5.8 Navigating after an action

The ViewModel exposes the *outcome* as state. The UI reacts to it and calls the navigation lambda.

```kotlin
data class EditArticleUiState(
    val title: String = "",
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,   // outcome, not a "navigate" command
)

@Composable
fun EditArticleRoute(
    onSaved: () -> Unit,
    viewModel: EditArticleViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onSaved()
    }

    EditArticleScreen(uiState = uiState, onTitleChange = viewModel::onTitleChange, onSave = viewModel::save)
}
```

### 5.9 Dialogs, bottom sheets and back handling

- Show a **simple dialog or bottom sheet** with local state (`var showDialog by rememberSaveable { … }`).
- Use a `dialog<XxxDestination>` destination only if the dialog needs its own ViewModel or a deep link.
- Support **predictive back**: set `android:enableOnBackInvokedCallback="true"` and use `BackHandler`/`PredictiveBackHandler` only when you really need to intercept back (for example "discard unsaved changes?").

### 5.10 Testing navigation

- **Feature screens:** in Compose UI tests, check that the right lambda is called (`onArticleClick` received `ArticleId("42")`). No NavController needed.
- **App wiring:** test `AppNavHost` with `TestNavHostController` from `androidx.navigation:navigation-testing`. Click through the UI and assert `navController.currentBackStackEntry?.destination?.hasRoute<ArticleDetailDestination>()`.
- **Deep links:** add one instrumented test per deep-link pattern.

> **Navigation 3** (`androidx.navigation3`) lets the app own the back stack as a plain list of keys. Evaluate it for new projects, and record the decision in an ADR ([§14](#14-architecture-decision-records-adr)). The principles in §5.1 apply to both versions.

---

## 6. Cross-cutting concerns

### 6.1 Dependency injection (Hilt)

```kotlin
@Module
@InstallIn(SingletonComponent::class)
internal interface DataModule {
    @Binds
    fun bindsArticlesRepository(impl: OfflineFirstArticlesRepository): ArticlesRepository
}

@Qualifier @Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val dispatcher: AppDispatchers)
enum class AppDispatchers { Default, IO }

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {
    @Provides @Dispatcher(AppDispatchers.IO)
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides @Dispatcher(AppDispatchers.Default)
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    fun providesClock(): Clock = Clock.System
}
```

- Always use **constructor injection**. No field injection, except where Android creates the class (`@AndroidEntryPoint` Activities).
- Always **inject dispatchers and `Clock`**. Never hard-code `Dispatchers.IO` or `Clock.System`, or tests can't control them.
- Use `@Binds` for interface → implementation and `@Provides` for third-party objects.
- Run Hilt/Dagger with **KSP**. It generates code at compile time and uses no reflection at runtime.

**Scoping**
- **Shared infrastructure is `@Singleton` and lives in `SingletonComponent`.** That includes `OkHttpClient`, `Retrofit`, API services, the Room database and DAOs, `DataStore`, and repositories that hold caches or state. Never install them in `ViewModelComponent`: every ViewModel would then build its own HTTP client, and the connection pool and cache would be wasted.
- **Prefer `@Inject constructor` + `@Binds`** over a `@Provides` function that only calls a constructor. Keep `@Provides` for third-party builders (`Retrofit.Builder`, `Room.databaseBuilder`).
- **Don't wrap every dependency in `dagger.Lazy<T>`.** Use `Lazy` only for something that is expensive to create, isn't always needed, and has a startup cost that profiling has shown.
- **No thread assertions inside DI providers.** StrictMode in debug builds catches disk and network access on the main thread ([§6.6](#66-debug-only-code-and-build-variants)).

### 6.2 Concurrency (coroutines and Flow)

- `suspend` for one-shot calls, `Flow` for streams.
- ViewModel → `viewModelScope`. Work that must outlive the ViewModel → the injected `@ApplicationScope` ([§6.4](#64-work-that-outlives-the-viewmodel)).
- Expose UI state as a `StateFlow`. Back it with a `MutableStateFlow` that coroutines in the ViewModel's error-handling scope update ([§2.1](#21-ui-layer)). Use `stateIn` only for derived state that cannot fail.
- **Never swallow `CancellationException`.** In suspend code, use `suspendRunCatching` instead of plain `runCatching` ([§6.3.6](#636-use-runcatching-not-trycatch)).
- Synchronize with `Mutex`, not `synchronized` or `java.util.concurrent` locks ([§10](#10-kotlin-multiplatform-friendly-code)).

### 6.3 Error handling

**Principles**

1. **Throw exceptions, don't return `Result`.** The data and domain layers return plain values or `Flow` and **throw** on failure. Callers don't have to unwrap every call, and errors travel up to the ViewModel on their own.
2. **Handle errors in the ViewModel, in one place.** Each ViewModel has **one `CoroutineExceptionHandler`** and launches every coroutine in `viewModelScope + exceptionHandler`.
3. **Always log and report general errors.** The general handler always logs the error and sends it to **Crashlytics as a non-fatal** through `ErrorReporter`.
4. **Never swallow an exception.** Every exception is either handled on purpose or reaches the general handler ([§6.3.5](#635-never-swallow-exceptions)).
5. **Specific handling is the exception, not the rule.** Catch a specific exception only when the UI must react to it differently ([§6.3.4](#634-specific-error-handling-only-when-needed)).

#### 6.3.1 Domain exceptions

Domain exceptions are defined in `:core:model`, in pure Kotlin:

```kotlin
sealed class AppException(message: String? = null, cause: Throwable? = null) : Exception(message, cause)

class NoConnectionException(cause: Throwable) : AppException("No internet connection", cause)
class ServerException(val code: Int, cause: Throwable) : AppException("Server error $code", cause)
class SessionExpiredException(cause: Throwable? = null) : AppException("Session expired", cause)
class InvalidCredentialsException : AppException("Invalid credentials")
```

The data layer translates low-level exceptions at its boundary and **always keeps the original as `cause`**, so the full stack trace reaches Crashlytics:

```kotlin
// :core:network
internal suspend fun <T> mapNetworkErrors(block: suspend () -> T): T =
    suspendRunCatching { block() }.getOrElse { e ->
        throw when (e) {
            is IOException -> NoConnectionException(e)
            is HttpException -> if (e.code() == 401) SessionExpiredException(e) else ServerException(e.code(), e)
            else -> e   // not a network error: rethrow unchanged
        }
    }

internal class RetrofitArticlesRemoteDataSource @Inject constructor(
    private val api: ArticlesApi,
) : ArticlesRemoteDataSource {
    override suspend fun getArticles(): List<ArticleDto> = mapNetworkErrors { api.getArticles() }
}
```

#### 6.3.2 Logging and reporting: `ErrorReporter`

```kotlin
// :core:common — pure Kotlin interface
interface ErrorReporter {
    fun log(message: String)            // breadcrumb: Logcat + Crashlytics log
    fun report(throwable: Throwable)    // Logcat + Crashlytics non-fatal
}

// :core:logging — Android implementation
internal class CrashlyticsErrorReporter @Inject constructor(
    private val crashlytics: FirebaseCrashlytics,
) : ErrorReporter {

    override fun log(message: String) {
        Log.d(TAG, message)
        crashlytics.log(message)
    }

    override fun report(throwable: Throwable) {
        Log.e(TAG, "Non-fatal error", throwable)
        crashlytics.recordException(throwable)   // shows up as "Non-fatal" in Crashlytics
    }

    private companion object { const val TAG = "ErrorReporter" }
}
```

- Use `ErrorReporter` for logging. Don't call `println`, `printStackTrace()` or Timber, and don't call `Log` directly outside `:core:logging`.
- Turn off Crashlytics collection in debug builds (`crashlytics.setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)`). Errors still go to Logcat.
- **Never log personal data** (emails, tokens, names, locations). To link a crash to a user, use `crashlytics.setUserId()` with an anonymous ID.

#### 6.3.3 General handler in every ViewModel

Every ViewModel follows this pattern (full example in [§2.1](#21-ui-layer)):

```kotlin
private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
    errorReporter.report(throwable)                                            // 1. log + Crashlytics non-fatal
    _uiState.update { it.copy(isLoading = false, error = throwable.toUiText()) } // 2. show it to the user
}
private val scope = viewModelScope + exceptionHandler
```

Map errors to user-facing text in `:core:ui`:

```kotlin
fun Throwable.toUiText(): UiText = when (this) {
    is NoConnectionException -> UiText.Res(R.string.error_no_connection)
    is SessionExpiredException -> UiText.Res(R.string.error_session_expired)
    else -> UiText.Res(R.string.error_generic)
}
```

Rules:
- **Launch every coroutine with `scope.launch { }`.** A bare `viewModelScope.launch { }` skips the handler and is not allowed.
- The handler only catches exceptions from **`launch`** coroutines at the top level. Never start an `async` at the top level: wrap it in `scope.launch { }` and call `await()` inside.
- `CancellationException` never reaches the handler. This is correct, because cancellation is not an error.
- Never install a `Thread.setDefaultUncaughtExceptionHandler` that hides crashes. Crashlytics reports real crashes as fatal automatically.

#### 6.3.4 Specific error handling (only when needed)

When the UI must react to one specific error differently, **handle that specific exception type** with `suspendRunCatching` inside `scope.launch` ([§6.3.6](#636-use-runcatching-not-trycatch)). Rethrow everything else so it reaches the general handler:

```kotlin
fun onLoginClick() {
    scope.launch {
        suspendRunCatching { login(uiState.value.email, uiState.value.password) }
            .onSuccess { _uiState.update { state -> state.copy(isLoggedIn = true) } }
            .onFailure { e ->
                if (e !is InvalidCredentialsException) throw e   // anything else → exceptionHandler → Crashlytics
                errorReporter.log("Login rejected: invalid credentials")  // expected → log, not a non-fatal
                _uiState.update { it.copy(passwordError = UiText.Res(R.string.error_invalid_password)) }
            }
    }
}
```

**Using `Result` or a sealed outcome type:** do this only when an operation has several *expected* outcomes that the caller must branch on, and exceptions would be awkward (for example a form validation that returns a list of field errors). **Prefer to avoid it.** Try catching a specific exception first, and explain the choice in the PR.

#### 6.3.5 Never swallow exceptions

| ❌ Swallowing | ✅ Instead |
|--------------|-----------|
| An `onFailure { }` or `catch` that ignores the error or only logs it | Let it propagate to the general handler |
| `runCatching { … }.getOrNull()` / `.getOrDefault(x)` | Let it throw, or catch the *specific* expected type |
| A Flow `.catch { emit(fallback) }` that doesn't report the error | Let it propagate. If a fallback is truly needed, call `errorReporter.report(e)` first |
| Plain `runCatching` or `catch (e: Throwable)` in suspend code (it swallows `CancellationException`) | `suspendRunCatching`, which rethrows `CancellationException` |
| Mapping an exception without keeping its `cause` | `throw DomainException(cause = e)` |
| `?: return` that silently hides a failure | `requireNotNull` / `checkNotNull`, or throw a domain exception |

**Every `onFailure` / `getOrElse` must do exactly one of these:**
- rethrow the exception,
- wrap it in a domain exception with `cause` and rethrow, or
- handle a **specific, expected** exception *and* log it through `ErrorReporter`, then rethrow everything else.

#### 6.3.6 Use `runCatching`, not `try/catch`

When you need to handle an error locally, **don't write `try/catch`**. Use `runCatching` and its operators instead: `onSuccess`, `onFailure`, `getOrElse`, `getOrThrow`, `recoverCatching`. The result is a single expression, and the handling is explicit and easy to review.

In **suspend code**, use our `suspendRunCatching` instead of plain `runCatching`. Plain `runCatching` also catches `CancellationException`, which breaks coroutine cancellation.

```kotlin
// :core:common
// inline, so the block can call suspend functions
inline fun <T> suspendRunCatching(block: () -> T): Result<T> =
    runCatching(block).onFailure { if (it is CancellationException) throw it }
```

Rules:
- The `Result` that `runCatching` returns is a **local tool only**. Finish it in the same function with `getOrThrow()`, `getOrElse { }` or `onFailure { }`. Never return it from a repository or use case (principle 1 in [§6.3](#63-error-handling)).
- **Always rethrow what you don't handle:** `onFailure { if (it !is ExpectedException) throw it }`.
- Never end with `getOrNull()` / `getOrDefault()` unless the failure has been reported first ([§6.3.5](#635-never-swallow-exceptions)).
- `try/catch` is not used. The only exception is `try { } finally { }` with no `catch`, for cleanup. Prefer `use { }` for anything `Closeable`.

#### 6.3.7 Enforcement (detekt)

```yaml
exceptions:
  SwallowedException:
    active: true
  TooGenericExceptionCaught:
    active: true            # we use runCatching, so this should never need suppressing
  PrintStackTrace:
    active: true
  InstanceOfCheckForException:
    active: true
empty-blocks:
  EmptyCatchBlock:
    active: true
    allowedExceptionNameRegex: '^$'   # no "ignored" catch blocks allowed
```

### 6.4 Work that outlives the ViewModel

This app doesn't need to sync large amounts of data in the background, so **we don't use WorkManager**. Short tasks that must keep running after the user leaves a screen run in the **application scope**.

`viewModelScope` is cancelled when the ViewModel is cleared. Use the injected **`@ApplicationScope`** when a task must finish anyway, for example:
- sending a "mark as read" or "like" request after the user has left the screen
- saving a draft
- refreshing a small cache

```kotlin
@Qualifier @Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationScope

@Module
@InstallIn(SingletonComponent::class)
object CoroutineScopesModule {
    @Provides @Singleton @ApplicationScope
    fun providesApplicationScope(
        @Dispatcher(AppDispatchers.Default) dispatcher: CoroutineDispatcher,
        errorReporter: ErrorReporter,
    ): CoroutineScope = CoroutineScope(
        SupervisorJob() + dispatcher +
            CoroutineExceptionHandler { _, e -> errorReporter.report(e) },  // Logcat + Crashlytics non-fatal
    )
}

// Excerpt from a repository: the data layer decides that the work must outlive the caller
internal class OfflineFirstArticlesRepository @Inject constructor(
    private val dao: ArticleDao,
    private val remote: ArticlesRemoteDataSource,
    @ApplicationScope private val appScope: CoroutineScope,
) : ArticlesRepository {

    override suspend fun markAsRead(id: ArticleId) {
        dao.markAsRead(id.value)                     // local update first: the UI reacts right away
        appScope.launch { remote.markAsRead(id) }    // finishes even if the user leaves the screen
    }
}
```

Rules:
- **Launch application-scope work from the data layer** (repositories), not from ViewModels. The ViewModel just calls a normal `suspend` function.
- **Fire-and-forget only:** use `appScope.launch { }`. If the UI needs the outcome, it observes the repository's state (single source of truth), not a return value.
- **Errors** go to the application scope's `CoroutineExceptionHandler`, which reports them to Crashlytics as non-fatals. Never wrap the body in a catch-all.
- **Keep tasks short** (seconds, not minutes). The application scope dies with the process. If the work must survive process death, save the intent to the database first and retry on the next app start.
- **Never use `GlobalScope`**, and never create an ad-hoc `CoroutineScope(...)`. Only use the injected `@ApplicationScope`.
- **In tests**, pass `backgroundScope` from `runTest` as the application scope.
- If the app later needs large or guaranteed background work, propose WorkManager in an ADR.

### 6.5 Configuration and secrets

- **Never put secrets in source code.** That means Kotlin constants, `strings.xml` and a committed `gradle.properties`. Read them from `local.properties` (gitignored) or CI secrets, and expose them through `BuildConfig` fields generated in a convention plugin.
- **A secret that was ever committed is leaked**, even if it's deleted later, because it stays in git history. Rotate it immediately.
- **Anything shipped in the APK can be extracted.** `BuildConfig` keeps keys out of the repo, not out of reach of attackers. Keys that grant paid or write access belong behind our own backend.
- Turn on secret scanning with push protection for the repository.
- Use build flavors (`dev`, `staging`, `prod`) for environment endpoints.

### 6.6 Debug-only code and build variants

- **`main` contains production code only.** That means:
  - no fake or placeholder data (such as random ratings or hardcoded lists)
  - no injected delays or errors
  - no commented-out code (git history keeps it)
  - no `TODO` without a ticket
- **Debug tools live in debug-only places:** `debugImplementation` dependencies or `src/debug/`. Examples are LeakCanary, StrictMode, HTTP body logging and debug menus. If `main` code must call one, put a no-op with the same API in `src/release/`. Prefer this over `if (BuildConfig.DEBUG)` branches, so the tools aren't in the release APK at all.
- **Enable StrictMode in debug builds** (`detectAll()`), and report violations through `ErrorReporter.log`.
- **Test hooks never go in `main`.** Tests use fakes from the test source sets.

```
core/debugtools/
 ├── src/debug/kotlin/…/DebugTools.kt     → real implementation (LeakCanary, StrictMode)
 └── src/release/kotlin/…/DebugTools.kt   → same API, empty bodies
```

---

## 7. Dependency policy

Every third-party library adds APK size, supply-chain risk, licence obligations, transitive dependencies and upgrade work, and might stop being maintained. **We keep the dependency list short.**

### 7.1 Allowed without approval

| Group | Examples |
|-------|----------|
| `androidx.*` | Compose, Lifecycle, Navigation, Room, DataStore, Paging, `androidx.test` |
| `org.jetbrains.kotlinx:*` (kotlinx) | coroutines, serialization, datetime, collections-immutable, kotlinx-io, atomicfu |
| `com.squareup.retrofit2:*` (Retrofit) | retrofit, `converter-kotlinx-serialization` |
| Square (`com.squareup.*`, and `app.cash.*` for Square/Cash App open source) | OkHttp, `logging-interceptor`, Okio, MockWebServer, Turbine, Paparazzi |
| Kotlin itself (`org.jetbrains.kotlin:*`) | stdlib, `kotlin-test`, the Compose and serialization compiler plugins |
| Firebase (`com.google.firebase:*`, via `firebase-bom`) | Crashlytics, Analytics, Remote Config, Cloud Messaging, plus the `google-services` and `firebase-crashlytics` Gradle plugins |

**Firebase notes:**
- Play Services libraries that Firebase pulls in transitively are fine. Adding `com.google.android.gms:*` directly still needs an ADR.
- Our code uses Firebase only through our own interfaces (`ErrorReporter`, `AnalyticsHelper`). Firebase imports are allowed only in the module that implements them (`:core:logging`) and in `:app`.

### 7.2 Everything else is forbidden by default

That includes popular libraries such as Gson, Moshi-reflect, Koin, RxJava, Glide, Lottie, Timber, direct Play Services, Ktor, Arrow and Accompanist.

**Before asking for a new dependency, check:**
1. Can a Kotlin language feature or an `androidx`/`kotlinx` API do it?
2. Can we write it in **~200 lines or fewer**? If so, write it in `:core:common` and own it.
3. If not, write an **ADR** ([§14](#14-architecture-decision-records-adr)) that covers the licence, maintenance activity, transitive dependencies, APK size impact, reflection use (must be none, see [§8](#8-no-reflection)), R8 compatibility and KMP support.
4. Once the ADR is accepted, add the library to the [§7.3](#73-approved-exceptions) table.

**Wrap every approved third-party library behind our own interface**, so we can replace it and fake it in tests.

### 7.3 Approved exceptions

| Dependency | Scope | Reason | Status |
|------------|-------|--------|--------|
| `com.google.dagger:hilt-*` (+ `androidx.hilt:*`) | runtime | DI used throughout this guide. Compile-time (KSP), no reflection | ✅ **Approved** (2026-11-21) |
| `junit:junit` (JUnit 4) **or** `org.junit.jupiter` (JUnit 5) | test | JUnit 4 is required by the `androidx.test` runner. JUnit 5 is more modern, but instrumented tests need an extra third-party plugin | ⚠️ **To decide**: one framework for the whole project ([§14](#14-architecture-decision-records-adr)) |
| `io.coil-kt.coil3:*` (Coil) | runtime | Image loading for Compose. Uses OkHttp, multiplatform, no reflection | ⚠️ **To decide** with an ADR ([§14](#14-architecture-decision-records-adr)) |
| `org.robolectric:robolectric` | test | JVM tests that need Android framework classes | ⚠️ To confirm |
| `io.mockk:mockk` | test | **Only** for `Context` and time ([§11.2](#112-fakes-over-mocks--strict-mocking-rules)) | ⚠️ To confirm |
| detekt, ktlint/Spotless | build tooling | Static analysis. Not shipped in the APK | ⚠️ To confirm |

### 7.4 Enforcement

- Declare every library in `gradle/libs.versions.toml`. Never write a version inline in a `build.gradle.kts` file.
- Add `gradle/libs.versions.toml` to `CODEOWNERS` so architecture owners review every change.
- Check transitive dependencies on upgrades: `./gradlew :app:dependencies --configuration releaseRuntimeClasspath`.
- Enable [Gradle dependency verification](https://docs.gradle.org/current/userguide/dependency_verification.html) (`verification-metadata.xml`).

---

## 8. No reflection

**Do not use reflection anywhere in production code.**

**Why:**
- R8 shrinking and obfuscation break reflection silently in release builds.
- Reflection is slow and hurts app startup.
- `kotlin-reflect` is a large JVM-only dependency, which blocks Kotlin Multiplatform.
- It turns compile-time errors into runtime crashes.

| ❌ Forbidden | ✅ Use instead |
|-------------|---------------|
| `kotlin-reflect` dependency, `kotlin.reflect.full.*` | Sealed hierarchies, `when`, explicit code |
| `java.lang.reflect.*`, `Class.forName`, `getDeclaredField/Method` | Interfaces and DI |
| Gson, Moshi-reflect, Jackson | `kotlinx.serialization` (compiler plugin) |
| Runtime annotation scanning | KSP / compiler plugins (code generation) |
| `enumValueOf` by name from untrusted input, or `valueOf` with `try/catch` | `entries.firstOrNull { it.key == value }` |
| Accessing private APIs or hidden framework fields | Public API, or file a feature request |

**Allowed (no runtime reflection, or an accepted exception):**
- Compile-time code generation: KSP (Hilt, Room), the kotlinx.serialization plugin, the Compose compiler, `@Parcelize`.
- `reified` type parameters and `KClass` used only as a type key (`toRoute<T>()`, `hiltViewModel<T>()`, `serializer<T>()`).
- **Retrofit:** it uses dynamic proxies internally. We accept this because it is explicitly allowed and ships its own R8 rules. Keep API interfaces simple, use `suspend` functions and the kotlinx.serialization converter.
- **Firebase SDKs:** they use reflection internally for component discovery. We accept this because Firebase is explicitly allowed and ships its own R8 rules. Our code never uses reflection on it.
- **Tests only:** MockK, under the restrictions in [§11.2](#112-fakes-over-mocks--strict-mocking-rules).

**Enforce it with detekt:**

```yaml
# config/detekt.yml
style:
  ForbiddenImport:
    active: true
    imports:
      - value: 'kotlin.reflect.full.*'
        reason: 'No reflection. See ARCHITECTURE.md §8'
      - value: 'kotlin.reflect.jvm.*'
        reason: 'No reflection. See ARCHITECTURE.md §8'
      - value: 'java.lang.reflect.*'
        reason: 'No reflection. See ARCHITECTURE.md §8'
      - value: 'com.google.gson.*'
        reason: 'Use kotlinx.serialization'
  ForbiddenMethodCall:
    active: true
    methods:
      - value: 'java.lang.Class.forName'
        reason: 'No reflection. See ARCHITECTURE.md §8'
```

---

## 9. No boilerplate

Boilerplate is code that is repeated without adding meaning. It hides bugs, slows down reviews and makes changes expensive. **Write the least code that is still clear.**

### 9.1 Use the language

| ❌ Boilerplate | ✅ Kotlin |
|---------------|----------|
| Manual `equals/hashCode/toString`, getters, setters | `data class` |
| Builder classes, telescoping constructors and overloads | Default and named arguments |
| `XxxUtils` / `XxxHelper` static classes | Extension functions (top-level) |
| Hand-written forwarding methods | Interface delegation (`class A(b: B) : B by b`) |
| `if/else if` chains on types | `sealed interface` + exhaustive `when` |
| `String` IDs passed around and mixed up | `@JvmInline value class ArticleId(val value: String)` |
| Mutable list + loop + add | `map`, `filter`, `buildList { }`, `associateBy` |
| Manual null checks with early returns | `?.`, `?:`, `requireNotNull`, `checkNotNull` |
| Manual argument validation `if (...) throw` | `require(...)`, `check(...)` |

```kotlin
// ❌ Boilerplate
class ArticleFilter private constructor(val query: String?, val onlyBookmarked: Boolean) {
    class Builder {
        private var query: String? = null
        private var onlyBookmarked = false
        fun query(q: String) = apply { query = q }
        fun onlyBookmarked(b: Boolean) = apply { onlyBookmarked = b }
        fun build() = ArticleFilter(query, onlyBookmarked)
    }
}

// ✅ Kotlin
data class ArticleFilter(
    val query: String? = null,
    val onlyBookmarked: Boolean = false,
)
```

### 9.2 Don't add ceremony

- **No pass-through layers.** Don't write a use case that only calls the repository, or a data source that only calls the DAO.
- **No interface for a class with only one implementation**, unless it is a boundary that needs a fake or a different implementation: repositories, data sources and platform APIs (`Clock`, `NetworkMonitor`, `StringResolver`).
- **No base classes** (`BaseViewModel`, `BaseFragment`, `BaseRepository`). Use small extension functions or delegation instead.
- **No UI model** (`XxxUi`) when the domain model already has the right shape.
- **No comments that repeat the code.** Comment *why*, not *what*.

### 9.3 Share repeated code the right way

- **Rule of three:** duplicating code once is fine. The third time, extract it.
- Shared Kotlin helpers go in `:core:common` (e.g. `suspendRunCatching`, `Flow.stateInWhileSubscribed`), or in the `:core:*` module they belong to (e.g. `mapNetworkErrors` in `:core:network`).
- Shared UI goes in `:core:designsystem` / `:core:ui`.
- Shared Gradle setup goes in convention plugins in `build-logic/`.
- **Never solve boilerplate with reflection** ([§8](#8-no-reflection)) **or a new third-party library** ([§7](#7-dependency-policy)). Use language features, small inline helpers, or compile-time code generation that is already approved.

---

## 10. Kotlin Multiplatform-friendly code

We build an Android app today. Code written like this costs nothing extra now, and lets us move `:core:model`, `:core:domain` and much of `:core:data` to KMP (`commonMain`) later with almost no rewrite.

### 10.1 Rules for non-UI modules

- **No `android.*` imports** in `:core:model`, `:core:domain` and the business logic in `:core:data`.
- **Avoid `java.*` imports.** Use Kotlin or kotlinx equivalents:

| ❌ JVM-only | ✅ Multiplatform |
|------------|-----------------|
| `java.time.*`, `System.currentTimeMillis()` | `kotlin.time.Clock` / `Instant` (or `kotlinx-datetime`), `kotlin.time.Duration` |
| `java.util.UUID` | `kotlin.uuid.Uuid` |
| `java.io.File`, `InputStream` | Okio `Path`/`FileSystem` or `kotlinx-io` |
| `String.format(...)` | String templates, `padStart`, formatting helpers |
| `synchronized`, `java.util.concurrent.*` locks, `AtomicInteger` | `kotlinx.coroutines.sync.Mutex`, `atomicfu` / `kotlin.concurrent.atomics` |
| `java.util.Locale` used implicitly | Pass the locale or formatting in explicitly from the UI layer |
| Java-style checked exceptions and `@Throws` contracts | Kotlin (unchecked) exceptions from the pure-Kotlin `AppException` hierarchy |
| `Parcelable` domain models | Plain `@Serializable` data classes |

- **Prefer libraries that are already multiplatform:** kotlinx.coroutines, kotlinx.serialization, kotlinx-datetime, Okio, and the KMP-enabled androidx libraries (**Room**, **DataStore**, **Lifecycle ViewModel**, `androidx.collection`, `paging-common`).

### 10.2 Put platform code behind interfaces

Inject platform APIs as interfaces defined in pure Kotlin. Android implementations live in Android modules:

```kotlin
// :core:common (pure Kotlin → future commonMain)
interface NetworkMonitor { val isOnline: Flow<Boolean> }
interface StringResolver { fun get(key: StringKey): String }

// :core:data (Android)
internal class ConnectivityManagerNetworkMonitor @Inject constructor(
    @ApplicationContext private val context: Context,
) : NetworkMonitor { /* … */ }
```

- Prefer **interface + DI** over `expect`/`actual`. Use `expect`/`actual` only for tiny factory functions once we really are on KMP.
- **Firebase is Android-only.** Shared code depends only on the `ErrorReporter` / `AnalyticsHelper` interfaces.
- **Retrofit and Hilt are JVM/Android-only.** Keep Retrofit inside `RemoteDataSource` implementations. Keep DTOs as plain `@Serializable` classes so another HTTP client could reuse them. Keep Hilt `@Module`s in the Android-facing modules. Pure classes need nothing more than an `@Inject constructor`.
- **ViewModels:** extend `androidx.lifecycle.ViewModel`, which is multiplatform. Don't use `AndroidViewModel` or `Context`. Return `UiText`/keys, not resolved Android strings.
- **Composables:** keep `XxxScreen` composables free of `LocalContext`, `android.*` and `R` lookups where possible. Pass in already-resolved strings and resources. Keep Android-specific code in `XxxRoute` and `:app`.

### 10.3 Enforcement

- Configure pure modules with `kotlin("jvm")` in a convention plugin (`app.jvm.library`). The Android SDK is then not on the classpath, so `android.*` imports fail to compile.
- Add detekt `ForbiddenImport` entries for `java.time.*`, `java.util.UUID`, `java.io.File` and `java.util.concurrent.*` in pure modules.

---

## 11. Testing

### 11.1 Strategy

| Level | What | Tools | Target |
|-------|------|-------|--------|
| Unit | ViewModels, use cases, repositories, mappers | `kotlin-test`, Turbine, `kotlinx-coroutines-test`, hand-written **fakes** | **Most tests** |
| Integration | DAOs, repository + real DB, API contracts | Room in-memory, **MockWebServer**, Robolectric | Critical data paths |
| UI | Stateless composables, navigation callbacks | Compose UI test, Robolectric | Key screens and states |
| Screenshot | Design system and screens | Paparazzi | Visual regressions |
| E2E | Critical user journeys | Espresso / UI Automator, Macrobenchmark | A few happy paths |

- Replace `Dispatchers.Main` with a `MainDispatcherRule` that uses `UnconfinedTestDispatcher`.
- Test **behaviour, not implementation**: assert on emitted `UiState` and stored data, not on which methods were called.

### 11.2 Fakes over mocks: strict mocking rules

**Hand-written fakes are the default.** A mocking library (MockK) may be used **only** to replace:

1. **`android.content.Context`** (and things it returns, like `Resources`), when a JVM test cannot avoid it and Robolectric would be overkill.
2. **Time sources** that cannot be injected (`SystemClock`, `System.currentTimeMillis()` inside framework code). Our own code takes an injected `Clock`, so it uses a `FixedClock` fake instead.

**Never mock:**
- our own classes: repositories, use cases, data sources, ViewModels, mappers
- DAOs or the database → use **in-memory Room**
- Retrofit APIs or HTTP → use **MockWebServer** (Square)
- Firebase (Crashlytics, Analytics) → use `FakeErrorReporter` / a fake `AnalyticsHelper`
- data classes, `Flow`s, `SavedStateHandle` (create a real one)
- `verify { }` call-count assertions → assert on resulting state instead

**Why:**
- Mocks tie tests to implementation details, so they break on every refactor.
- Mocks re-implement behaviour in every test instead of once in a fake.
- Mocks use bytecode manipulation and reflection ([§8](#8-no-reflection)).
- MockK is JVM-only, so mocked tests can't move to KMP ([§10](#10-kotlin-multiplatform-friendly-code)).

**Fakes live in `:core:testing`.** Every repository interface has one. They are simple and in-memory, and they can be controlled from the test:

```kotlin
class TestArticlesRepository : ArticlesRepository {
    private val articles = MutableSharedFlow<List<Article>>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    var error: Throwable? = null   // set it to make every call throw

    fun send(list: List<Article>) { articles.tryEmit(list) }

    override fun observeArticles(): Flow<List<Article>> = articles.onStart { error?.let { throw it } }
    override suspend fun sync() { error?.let { throw it } }
}

class FakeErrorReporter : ErrorReporter {
    val logs = mutableListOf<String>()
    val reported = mutableListOf<Throwable>()
    override fun log(message: String) { logs += message }
    override fun report(throwable: Throwable) { reported += throwable }
}

class FixedClock(var now: Instant) : Clock {
    override fun now(): Instant = now
}
```

**The only accepted uses of MockK:**

```kotlin
// ✅ Context
private val context: Context = mockk {
    every { getString(R.string.error_offline) } returns "You are offline"
}

// ✅ Framework time source we can't inject
mockkStatic(SystemClock::class)
every { SystemClock.elapsedRealtime() } returns 1_000L
```

Add MockK as a `testImplementation` dependency **only** in modules that touch `Context`. It must never appear in `:core:model`, `:core:domain` or the feature ViewModel tests.

**Example ViewModel test (fakes only):**

```kotlin
class ArticlesViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val repository = TestArticlesRepository()
    private val errorReporter = FakeErrorReporter()

    // Create inside the test so MainDispatcherRule is already active
    private fun createViewModel() = ArticlesViewModel(
        getArticles = GetArticlesUseCase(repository),
        bookmarkArticle = BookmarkArticleUseCase(repository),
        errorReporter = errorReporter,
    )

    @Test fun `shows articles when repository emits`() = runTest {
        val viewModel = createViewModel()
        repository.send(listOf(sampleArticle))

        assertEquals(listOf(sampleArticle.toUi()), viewModel.uiState.value.articles)
        assertTrue(errorReporter.reported.isEmpty())
    }

    @Test fun `reports non-fatal and shows error when loading fails`() = runTest {
        val error = NoConnectionException(IOException())
        repository.error = error

        val viewModel = createViewModel()

        assertEquals(listOf<Throwable>(error), errorReporter.reported)   // sent to Crashlytics
        assertNotNull(viewModel.uiState.value.error)                      // shown to the user
    }
}
```

---

## 12. Coding conventions

### Naming

| Type | Convention | Example |
|------|------------|---------|
| Navigation key | `XxxDestination` / `XxxGraph` | `ArticleDetailDestination` |
| Navigation API | `NavController.navigateToXxx()`, `NavGraphBuilder.xxxScreen()` / `xxxSection()` | `navigateToArticleDetail()` |
| Stateful / stateless composable | `XxxRoute` / `XxxScreen` | `ArticlesRoute`, `ArticlesScreen` |
| ViewModel | `XxxViewModel` | `ArticlesViewModel` |
| UI state | `XxxUiState` | `ArticlesUiState` |
| Use case | `VerbNounUseCase` | `GetUserFeedUseCase` |
| Repository | `XxxRepository` + `OfflineFirstXxxRepository` | `ArticlesRepository` |
| Data source | `XxxLocalDataSource` / `XxxRemoteDataSource` / `XxxDao` / `XxxApi` | `ArticleDao` |
| Models | `Xxx` (domain), `XxxDto`, `XxxEntity`, `XxxUi` | `ArticleDto` |
| Typed ID | `XxxId` value class | `ArticleId` |
| Exception | `XxxException : AppException` | `NoConnectionException` |
| Test doubles | `TestXxx` / `FakeXxx` | `TestArticlesRepository` |

### Package layout inside a feature module

```
feature/articles/
 ├── navigation/ArticlesNavigation.kt   (destinations + NavController/NavGraphBuilder extensions)
 ├── ArticlesRoute.kt                   (Route + Screen)
 ├── ArticlesViewModel.kt
 ├── ArticlesUiState.kt
 └── components/                        (private composables)
```

### General

- Kotlin official code style, enforced by **ktlint/Spotless** and **detekt** in CI.
- Every `@Composable` that emits UI takes `modifier: Modifier = Modifier` as its **first optional** parameter.
- No `!!` in production code.
- Prefer `sealed interface` over `sealed class`, and `data object` for singletons in hierarchies.
- Use public APIs from `:core:designsystem` for colors, spacing and typography. **No hard-coded values in feature modules.**

### Accessibility

- Every `Image`, `Icon` and `AsyncImage` has a meaningful `contentDescription` from string resources, or `null` if it is purely decorative. **Never `""`.**
- Clickable elements have a touch target of at least 48dp and a clear semantic label (`onClickLabel`, `Role`).
- Never convey information by color alone (for example a selected tab shown only in red).
- Test key screens with TalkBack, and turn on accessibility checks in Compose UI tests (`enableAccessibilityChecks()`).

### CI and baselines

- **CI only checks. It never fixes.** No CI step may rewrite tracked files before the checks run: no `*GenerateBaseline`, `ktlintFormat`, `fixDependencies` or similar tasks. Otherwise the checks can never fail.
- **Baselines only shrink.** A PR may remove baseline entries. Adding entries needs a reason that a reviewer accepts. New code has zero lint, detekt and ktlint issues.
- Every PR runs: debug and release builds, unit tests, Android Lint, detekt, ktlint and screenshot validation.
- Developers fix formatting locally (a pre-commit hook or `./gradlew ktlintFormat`) and commit the result.
- No commented-out CI steps. Either fix the step or remove it.

---

## 13. Pull request checklist

**Architecture**
- [ ] The UI does not access repositories or data sources directly
- [ ] The ViewModel exposes a single immutable `StateFlow<UiState>`
- [ ] New repositories have an interface, a DI binding and a fake in `:core:testing`
- [ ] Dispatchers and `Clock` are injected, and coroutines are cancellable-safe
- [ ] DTOs/entities are mapped before they leave the data layer
- [ ] No feature → feature module dependency added
- [ ] Module path, package and namespace match
- [ ] Shared infrastructure (OkHttp, Retrofit, DB, DataStore) is `@Singleton`. No `@Provides` that only calls a constructor, and no blanket `dagger.Lazy`
- [ ] Refresh fetches first, then replaces data in one `@Transaction`. One table per entity

**Errors**
- [ ] Data and domain code throws domain exceptions (with `cause`). No `Result` unless it is justified in the PR
- [ ] The ViewModel launches every coroutine through `viewModelScope + exceptionHandler`
- [ ] The general handler calls `errorReporter.report()` (Logcat + Crashlytics non-fatal)
- [ ] No swallowed exceptions: no empty catch, no `runCatching { }.getOrNull()`, no unreported Flow `.catch`
- [ ] Local error handling uses `runCatching` / `suspendRunCatching`, not `try/catch`
- [ ] Specific expected exceptions are handled by type and logged, and everything else is rethrown
- [ ] Work that must outlive the screen runs in the injected `@ApplicationScope`, launched from the data layer
- [ ] No personal data in logs or Crashlytics

**Navigation**
- [ ] Destinations are `@Serializable` and carry only IDs or primitives
- [ ] The ViewModel has no `NavController` and reads arguments via `SavedStateHandle.toRoute()`
- [ ] The feature exposes navigation through lambdas and `:app` wires them together
- [ ] Deep-link arguments are validated, and back behaves sensibly

**Rules**
- [ ] No new dependency outside `androidx` / `kotlinx` / Retrofit / Square, unless it has an accepted ADR ([§7](#7-dependency-policy))
- [ ] No reflection ([§8](#8-no-reflection))
- [ ] No boilerplate: no base classes, builders, pass-through use cases or single-implementation interfaces without a reason ([§9](#9-no-boilerplate))
- [ ] No `android.*` / `java.*` imports in pure modules ([§10](#10-kotlin-multiplatform-friendly-code))
- [ ] No secrets in source ([§6.5](#65-configuration-and-secrets))
- [ ] No fake data, injected delays or errors, or commented-out code in `main`. Debug tools only in debug source sets ([§6.6](#66-debug-only-code-and-build-variants))
- [ ] Images and icons have a meaningful `contentDescription`, or `null` if decorative
- [ ] CI steps only check (no baseline regeneration or auto-formatting), and no baseline grew

**Tests**
- [ ] Unit tests cover the new logic, and UI state changes are tested
- [ ] Tests use fakes. MockK is used only for `Context` or time ([§11.2](#112-fakes-over-mocks--strict-mocking-rules))
- [ ] Strings are in resources and dimensions/colors come from the design system

---

## 14. Architecture Decision Records (ADR)

Record significant decisions in `docs/adr/NNNN-title.md`. Every new dependency outside the allowlist needs one:

```markdown
# 0001 – Use Hilt for dependency injection
Status: Accepted · Date: 2026-11-21
## Context
## Decision
## Alternatives considered
## Consequences (APK size, reflection, KMP impact, licence)
```

### Open decisions

| Topic | Options | Notes |
|-------|---------|-------|
| Image loading | Coil (needs an exception) vs. our own small loader built on OkHttp | Image loading with caching and Compose support is hard to write in ~200 lines, so this probably needs an ADR to approve Coil |
| Test framework | JUnit 4 vs. JUnit 5 | Pick one for both unit and instrumented tests |
| Explicit API mode | `kotlin { explicitApi() }` for `:core:*` modules, or not | Forces visibility modifiers and return types on public APIs, and works well with API dump checks. Clearer module boundaries, but more ceremony ([§9](#9-no-boilerplate)) |

---

## 15. References and example projects

- 📘 [Guide to App Architecture (official)](https://developer.android.com/topic/architecture)
- 📘 [Architecture recommendations](https://developer.android.com/topic/architecture/recommendations)
- 📘 [Guide to Android app modularization](https://developer.android.com/topic/modularization)
- 📘 [Type-safe navigation in Compose](https://developer.android.com/guide/navigation/design/type-safety)
- 📘 [Navigation and the back stack](https://developer.android.com/guide/navigation/backstack)
- 📘 [Kotlin Multiplatform: Jetpack libraries with KMP support](https://developer.android.com/kotlin/multiplatform)
- 📘 [Test doubles on Android (fakes vs mocks)](https://developer.android.com/training/testing/fundamentals/test-doubles)
- 🧩 [Now in Android](https://github.com/android/nowinandroid): the reference for this guide (offline-first, modular, Compose, type-safe navigation, fakes instead of mocks)
- 🧩 [android/architecture-samples](https://github.com/android/architecture-samples): a TODO app showing MVVM + Repository
- 🧩 [android/compose-samples](https://github.com/android/compose-samples): Compose UI patterns
- 📝 [ViewModel: One-off event antipatterns](https://medium.com/androiddevelopers/viewmodel-one-off-event-antipatterns-16a1da869b95)
- 📝 [Kotlin coroutines best practices on Android](https://developer.android.com/kotlin/coroutines/coroutines-best-practices)
