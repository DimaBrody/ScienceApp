# Science App | Arxiv Summaries

**Jetpack Compose Multimodule App** for Android. Summarize scientific papers using lates LLM models like [OpenAI GPT](https://openai.com/) and [Google Gemini](https://ai.google.dev/docs/gemini_api_overview) with [Langdroid Library](https://github.com/DimaBrody/LangDroid).

Explore the latest and most influential papers on [Arxiv](https://arxiv.org/) and get main ideas and discoveries made by scientists from the whole world!

Example of app summarization process:

![Arxiv App](https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExajM0cWt1bjRpZzc4aTE3anU0a2wxZzNxNWZuNjE5M3N4OXQ4cm10dSZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/CaEgFXJoyI4LnRqTJF/giphy.gif)


### Architecture

The app is built following `:app` → `:feature` → `:shared` → `:core` architecture in general. Data handling is implemented with [UDF with SSOT (Unidirection Flow with Single Source of Truth)](https://developer.android.com/topic/architecture#unidirectional-data-flow) principle. Some situational modules like `:navigation` one below can be used as the outer level for some non-default behaviors.

Each module can contain up to 3 main submodules: `:presentation`, `:domain`, `:data`, and `:models` submodule containing above ones' models.

There is *simplified* architecture diagram for one of features demonstrated in the GIF:

<!---
 https://i.ibb.co/mCPPmrs/modules-3.jpg
 https://i.ibb.co/3B0pw83/modules-2-1.jpg
 --->

![Modules](https://i.ibb.co/3B0pw83/modules-2-1.jpg)

Submodules like `:models` or `:presentation`, `:domain`, `:data` are not shown to keep diagram clear enough.


### Functionality & Libraries
- The UI library is **Jetpack Compose**, DI is **Dagger Hilt**, gradle files are `.kts`
- Scroll and filter papers and science categories requesting [Arxiv API](https://info.arxiv.org/help/api/basics.html) with **Retrofit** and parsing XML responses
- Save to **Room databases** and offline/errors handling
- Using **protobuf** for setting and encrypted protobuf for storing AI keys
- Download with **Download Manager**, and extracting text from PDFs on background.
- **Notifications** and Deep Links with Jetpack Compose **Navigation**.
- Implementation of  **Kotlin Coroutines** & **Listenable WorkManager**
- Custom conventional plugins with `:build-logic` and `libs.versions` control

### Design
You can check my Figma design by which app: ![Figma](https://img.shields.io/badge/figma-%23F24E1E.svg?style=flat&logo=figma&logoColor=white&color=black) 
