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
package com.intellij.diff.tools.util.text;

import com.intellij.diff.comparison.ComparisonManager;
import com.intellij.diff.comparison.ComparisonPolicy;
import com.intellij.diff.fragments.LineFragment;
import com.intellij.diff.tools.util.base.HighlightPolicy;
import com.intellij.diff.tools.util.base.IgnorePolicy;
import com.intellij.diff.tools.util.base.TextDiffSettingsHolder.TextDiffSettings;
import com.intellij.diff.util.DiffUserDataKeysEx;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.util.ArrayUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.intellij.diff.tools.util.base.HighlightPolicy.*;
import static com.intellij.diff.tools.util.base.IgnorePolicy.*;

public class SimpleTextDiffProvider extends TwosideTextDiffProviderBase implements TwosideTextDiffProvider {
  static final DiffUserDataKeysEx.DiffComputer DEFAULT_COMPUTER = (text1, text2, policy, innerChanges, indicator) -> {
    if (innerChanges) {
      return ComparisonManager.getInstance().compareLinesInner(text1, text2, policy, indicator);
    }
    else {
      return ComparisonManager.getInstance().compareLines(text1, text2, policy, indicator);
    }
  };

  private static final IgnorePolicy[] IGNORE_POLICIES = {DEFAULT, TRIM_WHITESPACES, IGNORE_WHITESPACES, IGNORE_WHITESPACES_CHUNKS};
  private static final HighlightPolicy[] HIGHLIGHT_POLICIES = {BY_LINE, BY_WORD, BY_WORD_SPLIT, DO_NOT_HIGHLIGHT};

  @NotNull private final DiffUserDataKeysEx.DiffComputer myDiffComputer;

  public SimpleTextDiffProvider(@NotNull TextDiffSettings settings,
                                @NotNull Runnable rediff) {
    this(settings, rediff, DEFAULT_COMPUTER);
  }

  public SimpleTextDiffProvider(@NotNull TextDiffSettings settings,
                                @NotNull Runnable rediff,
                                @NotNull DiffUserDataKeysEx.DiffComputer diffComputer) {
    this(settings, rediff, diffComputer, IGNORE_POLICIES, HIGHLIGHT_POLICIES);
  }

  private SimpleTextDiffProvider(@NotNull TextDiffSettings settings,
                                 @NotNull Runnable rediff,
                                 @NotNull DiffUserDataKeysEx.DiffComputer diffComputer,
                                 @NotNull IgnorePolicy[] ignorePolicies,
                                 @NotNull HighlightPolicy[] highlightPolicies) {
    super(settings, rediff, ignorePolicies, highlightPolicies);
    myDiffComputer = diffComputer;
  }

  @NotNull
  @Override
  protected List<LineFragment> doCompare(@NotNull CharSequence text1,
                                         @NotNull CharSequence text2,
                                         @NotNull IgnorePolicy ignorePolicy,
                                         boolean innerFragments,
                                         @NotNull ProgressIndicator indicator) {
    ComparisonPolicy policy = ignorePolicy.getComparisonPolicy();
    return myDiffComputer.compute(text1, text2, policy, innerFragments, indicator);
  }


  public static class NoIgnore extends SimpleTextDiffProvider implements TwosideTextDiffProvider.NoIgnore {
    public NoIgnore(@NotNull TextDiffSettings settings, @NotNull Runnable rediff) {
      this(settings, rediff, DEFAULT_COMPUTER);
    }

    public NoIgnore(@NotNull TextDiffSettings settings, @NotNull Runnable rediff, @NotNull DiffUserDataKeysEx.DiffComputer diffComputer) {
      super(settings, rediff, diffComputer, IGNORE_POLICIES, ArrayUtil.remove(HIGHLIGHT_POLICIES, DO_NOT_HIGHLIGHT));
    }

    @NotNull
    @Override
    public List<LineFragment> compare(@NotNull CharSequence text1, @NotNull CharSequence text2, @NotNull ProgressIndicator indicator) {
      //noinspection ConstantConditions
      return super.compare(text1, text2, indicator);
    }
  }
}
