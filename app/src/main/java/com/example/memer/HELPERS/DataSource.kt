package com.example.memer.HELPERS

import com.example.memer.MODELS.*

class DataSource {
    companion object {
        fun createDataSet(): ArrayList<PostContents> {
            val list = ArrayList<PostContents>()
            list.add(
                PostContents(
                    mPost = "https://raw.githubusercontent.com/mitchtabian/Blog-Images/master/digital_ocean.png",
                    username = "User 1",
                )
            )
            list.add(
                PostContents(
                    mPost = "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/time_to_build_a_kotlin_app.png",
                    username = "User 2",
                )

            )

            list.add(
                PostContents(
                    mPost = "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/coding_for_entrepreneurs.png",
                    username = "User 2",
                )

            )
            list.add(
                PostContents(
                    mPost = "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/freelance_android_dev_vasiliy_zukanov.png",
                    "Steven"
                )
            )
            list.add(
                PostContents(
                    mPost = "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/freelance_android_dev_donn_felker.png",
                    username = "Richelle"
                )
            )
            list.add(
                PostContents(
                    mPost = "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/work_life_balance.png",
                    username = "Jessica"
                )
            )
            list.add(
                PostContents(
                    "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/fullsnack_developer.png",
                    "Guy"
                )
            )
            list.add(
                PostContents(
                    "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/javascript_expert_wes_bos.png",
                    "Ruby"
                )
            )
            list.add(
                PostContents(
                    "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/senior_android_engineer_kaushik_gopal.png",
                    "mitch"
                )
            )
            return list
        }

        fun createOnlyImageSet(): ArrayList<String> {
            val list = ArrayList<String>()
            list.add("https://raw.githubusercontent.com/mitchtabian/Blog-Images/master/digital_ocean.png")
            list.add("https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/time_to_build_a_kotlin_app.png")
            list.add("https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/coding_for_entrepreneurs.png")
            list.add("https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/freelance_android_dev_vasiliy_zukanov.png")
            list.add("https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/freelance_android_dev_donn_felker.png")
            list.add("https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/work_life_balance.png")
            list.add("https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/fullsnack_developer.png")
            list.add("https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/javascript_expert_wes_bos.png")
            list.add("https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/senior_android_engineer_kaushik_gopal.png")

            return list
        }


        fun getFakeUserEditableInfo(userId: String?): UserEditableInfo {
            return UserEditableInfo(
                "Aditya Raj",
                "aditya_007_raj",
                "My Bio"
            )
        }

        fun getFakeUserNonEditInfo(userId: String?): UserNonEditInfo {
            return UserNonEditInfo(
                10,
                15,
                21
            )
        }

        fun getUserId(): String? {
            return "aRaj"
        }

        fun getUserImageReference(userId: String?): String? {
            return "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/time_to_build_a_kotlin_app.png"
        }

        fun createUserPagePosts(): ArrayList<PostThumbnail> {
            val list = ArrayList<PostThumbnail>()
            list.add(
                PostThumbnail(
                    "https://raw.githubusercontent.com/mitchtabian/Blog-Images/master/digital_ocean.png",
                    "1",
                    "7",
                true
                )
            )
            list.add(
                PostThumbnail(
                    "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/time_to_build_a_kotlin_app.png",
                    "2",
                    "7",
                    true
                )
            )
            list.add(
                PostThumbnail(
                    "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/coding_for_entrepreneurs.png",
                    "3",
                    "7",
                    true
                )
            )
            list.add(
                PostThumbnail(
                    "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/freelance_android_dev_vasiliy_zukanov.png",
                    "4",
                    "7",
                    true
                )
            )
            list.add(
                PostThumbnail(
                    "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/freelance_android_dev_donn_felker.png",
                    "5",
                    "7",
                    true
                )
            )
            list.add(
                PostThumbnail(
                    "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/work_life_balance.png",
                    "6",
                    "7",
                    true
                )
            )
            list.add(
                PostThumbnail(
                    "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/fullsnack_developer.png",
                    "7",
                    "7",
                    true
                )
            )
            list.add(
                PostThumbnail(
                    "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/javascript_expert_wes_bos.png",
                    "8",
                    "7",
                    true
                )
            )
            list.add(
                PostThumbnail(
                    "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/senior_android_engineer_kaushik_gopal.png",
                    "9",
                    "7",
                    true
                )
            )

            return list
        }

        fun createHomePageDataSet(): ArrayList<PostContents> {
            val list = ArrayList<PostContents>()
            list.add(
                PostContents(
                    "https://raw.githubusercontent.com/mitchtabian/Blog-Images/master/digital_ocean.png",
                    "aditya_Raj",
                    "https://raw.githubusercontent.com/mitchtabian/Blog-Images/master/digital_ocean.png",
                    "123",
                    12,
                    "",
                    null,//Comments("Noice","aJain") to Comments("Nice Picture HAHA LOL","aByahut"),
                    "",
                    "aRaj",
                    null,
                    "Just A Nice Pic",
                    false,
                    false,
                    false,
                    false,
                    true
                )
            )
            list.add(
                PostContents(
                    "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/time_to_build_a_kotlin_app.png",
                    "anmol_jain",
                    "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/time_to_build_a_kotlin_app.png",
                    "1234",
                    124,
                    "",
                    null,//Comments("Good Post","aRaj" ) to Comments("Nice Picture  lala  HAHA LOL","dSehgal"),
                    "",
                    "aJain",
                    null,
                    "Nice Outing",
                    true,
                    false,
                    true,
                    true,
                    false
                )
            )

            list.add(
                PostContents(
                    "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/coding_for_entrepreneurs.png",
                    "dhruv_Sehgal",
                    "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/coding_for_entrepreneurs.png",
                    "1324",
                    18,
                    "",
                    null,//Comments("Good Post","aRaj") to Comments("Nice Picture  lala  HAHA LOL","dSehgal"),
                    "",
                    "dSehgal",
                    null,
                    "Nice Place and Nice Food",
                    false,
                    true,
                    true,
                    false,
                    false
                )
            )
            list.add(
                PostContents(
                    "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/freelance_android_dev_vasiliy_zukanov.png",
                    "abhinav_Byahut",
                    "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/freelance_android_dev_vasiliy_zukanov.png",
                    "1274",
                    125,
                    "",
                    null,//Comments("Good Post","aRaj") to Comments("Nice Picture  lala  HAHA LOL","dSehgal"),
                    "",
                    "aByahut",
                    null,
                    "Nice Place and Nice Food",
                    false,
                    false,
                    false,
                    true,
                    true
                )
            )
            list.add(
                PostContents(
                    "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/freelance_android_dev_donn_felker.png",
                    "naman_Singhal",
                    "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/freelance_android_dev_donn_felker.png",
                    "1344",
                    1253,
                    "",
                    null,//Comments("Good Post","aRaj") to Comments("Nice Picture  lala  HAHA LOL","dSehgal"),
                    "",
                    "nSinghal",
                    null,
                    "Nice Place and Nice Food",
                    true,
                    true,
                    true,
                    false,
                    false
                )
            )
            list.add(
                PostContents(
                    "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/work_life_balance.png",
                    "naman_Singhal",
                    "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/freelance_android_dev_donn_felker.png",
                    "1444",
                    13,
                    "",
                    null,//Comments("Good Post","aRaj") to Comments("Nice Picture  lala  HAHA LOL","dSehgal"),
                    "",
                    "nSinghal",
                    null,
                    "Nice Place and Nice Food",
                    true,
                    true,
                    true,
                    false,
                    false
                )
            )
            return list
        }
    }
}