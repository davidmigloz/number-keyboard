/*
 * Copyright (c) 2017 David Miguel Lozano.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.davidmiguel.numberkeyboard;

/**
 * Enables to listen keyboard events.
 */
public interface NumberKeyboardListener {

    /**
     * Invoked when a number key is clicked.
     */
    void onNumberClicked(int number);

    /**
     * Invoked when the left auxiliary button is clicked.
     */
    void onLeftAuxButtonClicked();

    /**
     * Invoked when the right auxiliary button is clicked.
     */
    void onRightAuxButtonClicked();
}
