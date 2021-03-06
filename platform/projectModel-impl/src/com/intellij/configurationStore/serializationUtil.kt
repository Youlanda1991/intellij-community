/*
 * Copyright 2000-2017 JetBrains s.r.o.
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
package com.intellij.configurationStore

import com.intellij.openapi.components.BaseState
import com.intellij.openapi.components.ComponentSerializationUtil
import com.intellij.openapi.components.PersistentStateComponent
import org.jdom.Element

fun deserializeAndLoadState(component: PersistentStateComponent<*>, element: Element) {
  val state = deserialize(element, ComponentSerializationUtil.getStateClass<Any>(component.javaClass))
  (state as? BaseState)?.resetModificationCount()
  @Suppress("UNCHECKED_CAST")
  (component as PersistentStateComponent<Any>).loadState(state)
}
