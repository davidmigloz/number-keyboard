# Number Keyboard  [![](https://jitpack.io/v/davidmigloz/number-keyboard.svg)](https://jitpack.io/#davidmigloz/number-keyboard)

Android library that provides a number keyboard composable.

![screenshot](img/screenshot.jpg)

## Usage

#### Step 1

Add the JitPack repository to your `build.gradle ` file:

```gradle
allprojects {
	repositories {
		...
		maven { url "https://jitpack.io" }
	}
}
```

#### Step 2

Add the dependency:

```gradle
dependencies {
	implementation 'com.github.davidmigloz:number-keyboard:4.0.0'
}
```

[CHANGELOG](https://github.com/davidmigloz/number-keyboard/blob/master/CHANGELOG.md)

> **Note:** in v4.0.0 the library was migrated to Jetpack Compose. If you need to old Android View based version, please keep using v3.1.0 instead.

#### Step 3

Use `NumberKeyboard` composable in your layout:

```kotlin
NumberKeyboard(
  maxAllowedAmount = 999.00,
  maxAllowedDecimals = 0,
  roundUpToMax = false,
  button = { number, clickedListener ->
    NumberKeyboardButton(
      modifier = buttonModifier,
      textStyle = buttonTextStyle,
      number = number,
      listener = clickedListener
    )
  },
  leftAuxButton = { _ ->
    NumberKeyboardAuxButton(
      modifier = buttonModifier,
      textStyle = buttonTextStyle,
      imageVector = Icons.Rounded.Fingerprint,
      clicked = { Toast.makeText(context, "Triggered", Toast.LENGTH_SHORT).show() }
    )
  },
  rightAuxButton = { clickedListener ->
    NumberKeyboardAuxButton(
      modifier = buttonModifier,
      textStyle = buttonTextStyle,
      imageVector = Icons.Rounded.Backspace,
      clicked = { clickedListener.onRightAuxButtonClicked() }
    )
  },
  listener = object : NumberKeyboardListener {
    override fun onUpdated(data: NumberKeyboardData) {
      text = data.int.toString()
    }
  }
)
```

Take a look at the [sample app](https://github.com/davidmigloz/number-keyboard/tree/master/sample) to see the library working.

## Contributing

If you find any issues or you have any questions, ideas... feel free to [open an issue](https://github.com/davidmigloz/number-keyboard/issues/new).
Pull request are very appreciated.

## License

Copyright (c) 2023 David Miguel Lozano / Morgan Koh

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
