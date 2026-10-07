package com.example.tindahan.core

object SearchQuery {
    /** Escapes LIKE wildcards so "50%" searches for the text "50%". Pair with ESCAPE '\' in SQL. */
    fun escapeLike(raw: String): String =
        raw.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_")
}
