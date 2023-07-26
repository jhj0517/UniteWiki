# UniteWiki
This is an practice Android project that provides information about the popular game Pokémon Unite. 

**This app only provides information about the game and has nothing to do with the game itself.**

## Demo Video
https://github.com/jhj0517/UniteWiki/assets/97279763/d42cde7a-4d85-4d8f-898c-9be0c7d90ea5

## Feature
- Provide Pokémon information by type, in the form of `tabLayout` and `RecyclerView`.
- See the different abilities of each Pokémon, in the form of `tooltips`.
- Rate and write a review for Pokémon.
- Edit, delete and report a review.

## Technologies
| Technology | Usage |
| ---------- | ----- |
| MVVM (Model-View-ViewModel) | Separates the user interface logic from business logic |
| Coroutine | Manages background tasks and makes the app more responsive |
| Hilt | Manages dependency injection |

## What I learned

- How to build an MVVM architecture according to the principle of separation of concerns in Android Project.
- How to change the color of the tab in the `tabLayout` when the tab is selected.
- How to use `dataBinding` to implement UI changes through Binding Adapters.
- How to implement the search function in `RecyclerView` using `Filterable` in the adapter.
- How to nationalize app with different languages in Android
- How to inject dependencies into an app using `Hilt`.

## PlayStore
The app is available on the PlayStore. If you want to see how it works, you can check [here](https://play.google.com/store/apps/details?id=com.unitewikiapp.unitewiki&hl=ko&gl=US).
