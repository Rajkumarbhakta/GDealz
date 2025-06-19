package com.rkbapps.gdealz.models

import com.google.gson.annotations.SerializedName

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


data class SteamGameData(
    val success: Boolean = false,
    val data: Data? = null
)

data class Data(

    @SerializedName("type") val type: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("steam_appid") val steamAppid: Int? = null,
    @SerializedName("required_age") val requiredAge: String? = null,
    @SerializedName("is_free") val isFree: Boolean? = null,
    @SerializedName("detailed_description") val detailedDescription: String? = null,
    @SerializedName("about_the_game") val aboutTheGame: String? = null,
    @SerializedName("short_description") val shortDescription: String? = null,
    @SerializedName("reviews") val reviews: String? = null,
    @SerializedName("header_image") val headerImage: String? = null,
    @SerializedName("capsule_image") val capsuleImage: String? = null,
    @SerializedName("capsule_imagev5") val capsuleImagev5: String? = null,
    @SerializedName("website") val website: String? = null,
    @SerializedName("pc_requirements") val pcRequirements: PcRequirements? = PcRequirements(),
    @SerializedName("developers") val developers: ArrayList<String> = arrayListOf(),
    @SerializedName("publishers") val publishers: ArrayList<String> = arrayListOf(),
    @SerializedName("platforms") val platforms: Platforms? = Platforms(),
    @SerializedName("metacritic") val metacritic: Metacritic? = Metacritic(),
    @SerializedName("categories") val categories: ArrayList<Categories> = arrayListOf(),
    @SerializedName("genres") val genres: ArrayList<Genres> = arrayListOf(),
    @SerializedName("screenshots") val screenshots: ArrayList<Screenshots> = arrayListOf(),
    @SerializedName("movies") val movies: ArrayList<Movies> = arrayListOf(),
    @SerializedName("recommendations") val recommendations: Recommendations? = Recommendations(),
    @SerializedName("release_date") val releaseDate: ReleaseDate? = ReleaseDate()

)

data class PcRequirements(

    @SerializedName("minimum") val minimum: String? = null,
    @SerializedName("recommended") val recommended: String? = null

)

data class Platforms(

    @SerializedName("windows") val windows: Boolean? = null,
    @SerializedName("mac") val mac: Boolean? = null,
    @SerializedName("linux") val linux: Boolean? = null

)

data class Metacritic(

    @SerializedName("score") val score: Int? = null,
    @SerializedName("url") val url: String? = null

)

data class Categories(

    @SerializedName("id") val id: Int? = null,
    @SerializedName("description") val description: String? = null

)

data class Genres(

    @SerializedName("id") val id: String? = null,
    @SerializedName("description") val description: String? = null

)


data class Screenshots(

    @SerializedName("id") val id: Int? = null,
    @SerializedName("path_thumbnail") val pathThumbnail: String? = null,
    @SerializedName("path_full") val pathFull: String? = null

)

data class Webm(

    @SerializedName("480") val resolution480: String? = null,
    @SerializedName("max") val max: String? = null

)

data class Mp4(

    @SerializedName("480") val resolution480: String? = null,
    @SerializedName("max") val max: String? = null

)

data class Movies(

    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("thumbnail") val thumbnail: String? = null,
    @SerializedName("webm") val webm: Webm? = Webm(),
    @SerializedName("mp4") val mp4: Mp4? = Mp4(),
    @SerializedName("highlight") val highlight: Boolean? = null

)

data class Recommendations(

    @SerializedName("total") val total: Int? = null

)

data class ReleaseDate(

    @SerializedName("coming_soon") val comingSoon: Boolean? = null,
    @SerializedName("date") val date: String? = null

)



/*
@Serializable
data class SteamGameData(
    @SerialName("success")
    val success: Boolean = false,
    @SerialName("data")
    val data: Data? = null
)

@Serializable
data class Data(

    @SerialName("type") val type: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("steam_appid") val steamAppid: Int? = null,
    @SerialName("required_age") val requiredAge: String? = null,
    @SerialName("is_free") val isFree: Boolean? = null,
//    @Transient
//    val detailedDescription: String? = null,
//    @SerialName("about_the_game") val aboutTheGame: String? = null,
    @SerialName("short_description") val shortDescription: String? = null,
    @SerialName("reviews") val reviews: String? = null,
    @SerialName("header_image") val headerImage: String? = null,
    @SerialName("capsule_image") val capsuleImage: String? = null,
    @SerialName("capsule_imagev5") val capsuleImagev5: String? = null,
    @SerialName("website") val website: String? = null,
//    @SerialName("pc_requirements") val pcRequirements: PcRequirements? = PcRequirements(),
    @SerialName("developers") val developers: ArrayList<String> = arrayListOf(),
    @SerialName("publishers") val publishers: ArrayList<String> = arrayListOf(),
    @SerialName("platforms") val platforms: Platforms? = Platforms(),
    @SerialName("metacritic") val metacritic: Metacritic? = Metacritic(),
    @SerialName("categories") val categories: ArrayList<Categories> = arrayListOf(),
    @SerialName("genres") val genres: ArrayList<Genres> = arrayListOf(),
    @SerialName("screenshots") val screenshots: ArrayList<Screenshots> = arrayListOf(),
    @SerialName("movies") val movies: ArrayList<Movies> = arrayListOf(),
    @SerialName("recommendations") val recommendations: Recommendations? = Recommendations(),
    @SerialName("release_date") val releaseDate: ReleaseDate? = ReleaseDate()

)

@Serializable
data class PcRequirements(

    @SerialName("minimum") val minimum: String? = null,
    @SerialName("recommended") val recommended: String? = null

)

@Serializable
data class Platforms(

    @SerialName("windows") val windows: Boolean? = null,
    @SerialName("mac") val mac: Boolean? = null,
    @SerialName("linux") val linux: Boolean? = null

)

@Serializable
data class Metacritic(

    @SerialName("score") val score: Int? = null,
    @SerialName("url") val url: String? = null

)

@Serializable
data class Categories(

    @SerialName("id") val id: Int? = null,
    @SerialName("description") val description: String? = null

)

@Serializable
data class Genres(

    @SerialName("id") val id: String? = null,
    @SerialName("description") val description: String? = null

)

@Serializable
data class Screenshots(

    @SerialName("id") val id: Int? = null,
    @SerialName("path_thumbnail") val pathThumbnail: String? = null,
    @SerialName("path_full") val pathFull: String? = null

)

@Serializable
data class Webm(

    @SerialName("480") val resolution480: String? = null,
    @SerialName("max") val max: String? = null

)

@Serializable
data class Mp4(

    @SerialName("480") val resolution480: String? = null,
    @SerialName("max") val max: String? = null

)

@Serializable
data class Movies(

    @SerialName("id") val id: Int? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("thumbnail") val thumbnail: String? = null,
    @SerialName("webm") val webm: Webm? = Webm(),
    @SerialName("mp4") val mp4: Mp4? = Mp4(),
    @SerialName("highlight") val highlight: Boolean? = null

)

@Serializable
data class Recommendations(

    @SerialName("total") val total: Int? = null

)

@Serializable
data class ReleaseDate(

    @SerialName("coming_soon") val comingSoon: Boolean? = null,
    @SerialName("date") val date: String? = null

)*/
