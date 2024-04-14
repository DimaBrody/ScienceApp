package com.brody.arxiv.features.summary.presentation.models

const val DEFAULT_CHUNK_PROMPT = """
Write a concise summary of the following:
"{text}"
CONCISE SUMMARY:
"""

const val DEFAULT_FINAL_PROMPT = """
Write a very detailed summary of the following SCIENTIFIC PAPER delimited by triple backquotes.
Return your response which covers all key points and ideas as bullets of the paper.
```{text}```
SUMMARY without header and with detailed bullets:
"""

const val FINAL_PROMPT_1 = """
Write a very detailed summary of the following SCIENTIFIC PAPER delimited by triple backquotes.
Return your response which covers all key points and ideas.

Make it structured, make concise introduction, BULLET LIST with very detailed key points and ideas (!important to have list), and short concise thoughts at the end
```{text}```
SUMMARY without header and with intro, detailed numeric list with sections and bullets of its ideas, and main idea of paper:
"""