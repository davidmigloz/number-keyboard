# Change Log

## Version 5.0.2  *(16/07/2025)*

- Fixed  `rawAmount` having 0 in front.

### ⚠️ Breaking Changes

- Removed `onAmountChange: (String) -> Unit` and merge with `NumberKeyboardListener` implementation.

**🧭 Migration Guide**

```kotlin
var amountWithCurrency by remember { mutableStateOf("$currencySymbol 0") }
var amount by remember { mutableStateOf("") }

NumberKeyboard(
    amount = amount,
    listener = object : NumberKeyboardListener {
        override fun onUpdated(data: NumberKeyboardData) {
            amountWithCurrency = data.currency
            amount = data.rawAmount
        }
    }
)
```

## Version 5.0.1  *(15/07/2025)*

- Fix Android target configuration (#46)

## Version 5.0.0  *(15/07/2025)*

- Migrate to Kotlin Multiplatform (#45)

### ✨ New Features

• Introduced NumberKeyboardFormat enum to control keypad layout:

```kotlin
enum class NumberKeyboardFormat {
    Normal,         // Standard ascending layout (like phone dial pad)
    Inverted,       // Descending layout (like a calculator)
    Scrambled,      // Shuffled once on composition
    AlwaysScrambled // Re-shuffles every tap (chaos, but secure chaos)
}
```

### ⚠️ Breaking Changes

- `isInverted: Boolean` is now deprecated
  One flag was never enough. Now you’ve got four layout options to rule them all. Replace

```kotlin
isInverted = true
```

with:

```kotlin
format = NumberKeyboardFormat.Inverted
```

**🧭 Migration Guide**

| Before             | After                                  |
|--------------------|----------------------------------------|
| isInverted = false | format = NumberKeyboardFormat.Normal   |
| isInverted = true  | format = NumberKeyboardFormat.Inverted |

- `NumberKeyboard` is now a **stateless composable**.
    - Removed internal `remember` state for the input amount.
    - You **must** provide:
        - `amount: String`
        - `onAmountChange: (String) -> Unit`
    - Removed `initialAmount` attribute.
    - This enables external state management and improves integration with architectures like MVI,
      ViewModel, etc.

**Before:**

```kotlin
NumberKeyboard() // internally remembered state
```

**After:**

```kotlin
var amount by remember { mutableStateOf("") }

NumberKeyboard(
    amount = amount,
    onAmountChange = { amount = it }
)
```

## Version 4.0.8  *(16/06/2025)*

- Update dependencies and target SDK 36
- Fix import for LocalContentColor

## Version 4.0.7  *(15/06/2025)*

- Added `iconTint` and `colors` parameters to `NumberKeyboardAuxButton` to allow
  customization of delete button color and styling

## Version 4.0.6  *(07/03/2024)*

- Downgrade to Java 18

## Version 4.0.5  *(07/03/2024)*

- Downgrade androidGradle to v8.2.2

## Version 4.0.4  *(07/03/2024)*

- Fix JitPack Java SDK to v20.0.2

## Version 4.0.3  *(06/03/2024)*

- Fix max amount formatting with correct separator
- Add initialAmount support and cleanup unused ExperimentalLayoutApi warnings

## Version 4.0.2  *(03/11/2023)*

- Fix JitPack build

## Version 4.0.1  *(03/11/2023)*

- Fix JitPack build

## Version 4.0.0  *(03/11/2023)*

- Revamped Number Keyboard to Compose #37 (thanks @delacrixmorgan!)

## Version 3.1.0  *(13/12/2022)*

- Add text size attribute (thanks @gerynugrh)
- Add typeface attribute (thanks @gerynugrh)
- Add KeyboardCustomSquareActivity to show custom square design (thanks @delacrixmorgan)
- Update dependencies

## Version 3.0.0  *(30/12/2018)*

- Migrate to Kotlin #21
- Refactor support lib to AndroidX #13
- Extract dependencies and configurations from build files #22
- Add numberkeyboard_ resource prefix #23 (breaking change)

### Migration

Add the prefix `numberkeyboard_` to all the library attributes and resources.
E.g.
```
Before:  keyboard:keyboardType="integer"
Now:     keyboard:numberkeyboard_keyboardType="integer"
```

## Version 2.0.1  *(23/04/2018)*

- Fix layout issues when using dynamic height #10
- Fix Proguard is obfuscating everything  #9

## Version 2.0.0  *(20/04/2018)*

- Implement keys using Autosizing TextViews #8
- Adding `keyPadding` attribute
- Remove `numberKeyTextSize` attribute **(breaking change)**
- Update support libraries to 27.1.1 #7
- Update buildToolsVersion to 27.0.3 #7
- Update gradle to 4.6 #7

## Version 1.0.0  *(18/01/2018)*

- Update support libraries to 27.0.2
- Update buildToolsVersion to 26.0.2
- Update gradle to 4.1
- Improve decimal sample

## Version 0.3.0  *(25/09/2017)*

- Update support libraries to 26.1.0
- Update buildToolsVersion to 26.0.1

## Version 0.2.0  *(25/09/2017)*

- Library crashes when running on API 19 #1
- Update buildToolsVersion "25.0.3"

## Version 0.1.0  *(20/07/2017)*

- Initial release
