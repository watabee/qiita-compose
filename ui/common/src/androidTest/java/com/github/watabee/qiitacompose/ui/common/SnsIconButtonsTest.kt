package com.github.watabee.qiitacompose.ui.common

import androidx.compose.material.Surface
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.watabee.qiitacompose.ui.theme.QiitaTheme
import com.google.common.truth.Truth
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SnsIconButtonsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun test_whenGithubLoginNameExists_thenGithubButtonIsDisplayed() {
        var isClicked = false

        composeTestRule.setContent {
            QiitaTheme {
                Surface {
                    SnsIconButtons(
                        githubLoginName = "dummy",
                        twitterScreenName = "dummy",
                        facebookId = "dummy",
                        linkedinId = "dummy",
                        onGithubButtonClicked = { isClicked = true },
                    )
                }
            }
        }

        composeTestRule.onNodeWithTag(testTag = "GithubButton")
            .assertIsDisplayed()
            .performClick()

        Truth.assertThat(isClicked).isTrue()
    }

    @Test
    fun test_whenGithubLoginNameNotExists_thenGithubButtonIsNotDisplayed() {
        composeTestRule.setContent {
            QiitaTheme {
                Surface {
                    SnsIconButtons(
                        githubLoginName = null,
                        twitterScreenName = "dummy",
                        facebookId = "dummy",
                        linkedinId = "dummy",
                    )
                }
            }
        }

        composeTestRule.onNodeWithTag(testTag = "GithubButton")
            .assertDoesNotExist()
    }

    @Test
    fun test_whenTwitterNameExists_thenTwitterButtonIsDisplayed() {
        var isClicked = false

        composeTestRule.setContent {
            QiitaTheme {
                Surface {
                    SnsIconButtons(
                        githubLoginName = "dummy",
                        twitterScreenName = "dummy",
                        facebookId = "dummy",
                        linkedinId = "dummy",
                        onTwitterButtonClicked = { isClicked = true },
                    )
                }
            }
        }

        composeTestRule.onNodeWithTag(testTag = "TwitterButton")
            .assertIsDisplayed()
            .performClick()

        Truth.assertThat(isClicked).isTrue()
    }

    @Test
    fun test_whenTwitterNameNotExists_thenTwitterButtonIsNotDisplayed() {
        composeTestRule.setContent {
            QiitaTheme {
                Surface {
                    SnsIconButtons(
                        githubLoginName = "dummy",
                        twitterScreenName = null,
                        facebookId = "dummy",
                        linkedinId = "dummy",
                    )
                }
            }
        }

        composeTestRule.onNodeWithTag(testTag = "TwitterButton")
            .assertDoesNotExist()
    }

    @Test
    fun test_whenFacebookIdExists_thenFacebookButtonIsDisplayed() {
        var isClicked = false

        composeTestRule.setContent {
            QiitaTheme {
                Surface {
                    SnsIconButtons(
                        githubLoginName = "dummy",
                        twitterScreenName = "dummy",
                        facebookId = "dummy",
                        linkedinId = "dummy",
                        onFacebookButtonClicked = { isClicked = true },
                    )
                }
            }
        }

        composeTestRule.onNodeWithTag(testTag = "FacebookButton")
            .assertIsDisplayed()
            .performClick()

        Truth.assertThat(isClicked).isTrue()
    }

    @Test
    fun test_whenFacebookIdNotExists_thenFacebookButtonIsNotDisplayed() {
        composeTestRule.setContent {
            QiitaTheme {
                Surface {
                    SnsIconButtons(
                        githubLoginName = "dummy",
                        twitterScreenName = "dummy",
                        facebookId = null,
                        linkedinId = "dummy",
                    )
                }
            }
        }

        composeTestRule.onNodeWithTag(testTag = "FacebookButton")
            .assertDoesNotExist()
    }

    @Test
    fun test_whenLinkedinIdExists_thenLinkedinButtonIsDisplayed() {
        var isClicked = false

        composeTestRule.setContent {
            QiitaTheme {
                Surface {
                    SnsIconButtons(
                        githubLoginName = "dummy",
                        twitterScreenName = "dummy",
                        facebookId = "dummy",
                        linkedinId = "dummy",
                        onLinkedinButtonClicked = { isClicked = true },
                    )
                }
            }
        }

        composeTestRule.onNodeWithTag(testTag = "LinkedinButton")
            .assertIsDisplayed()
            .performClick()

        Truth.assertThat(isClicked).isTrue()
    }

    @Test
    fun test_whenLinkedinIdNotExists_thenLinkedinButtonIsNotDisplayed() {
        composeTestRule.setContent {
            QiitaTheme {
                Surface {
                    SnsIconButtons(
                        githubLoginName = "dummy",
                        twitterScreenName = "dummy",
                        facebookId = "dummy",
                        linkedinId = null,
                    )
                }
            }
        }

        composeTestRule.onNodeWithTag(testTag = "LinkedinButton")
            .assertDoesNotExist()
    }
}
