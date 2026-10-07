package com.example.tindahan.core

import org.junit.Assert.assertEquals
import org.junit.Test

class SearchQueryTest {
    @Test fun plainTextIsUnchanged() {
        assertEquals("coke 290", SearchQuery.escapeLike("coke 290"))
    }

    @Test fun wildcardsAreEscaped() {
        assertEquals("50\\%", SearchQuery.escapeLike("50%"))
        assertEquals("a\\_b", SearchQuery.escapeLike("a_b"))
    }

    @Test fun backslashIsEscapedFirst() {
        assertEquals("a\\\\b", SearchQuery.escapeLike("a\\b"))
        assertEquals("\\\\\\%", SearchQuery.escapeLike("\\%"))
    }
}
