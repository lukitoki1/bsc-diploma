package com.example.polyglot.data

import com.fasterxml.jackson.annotation.JsonProperty

data class TagSet(var image: List<Image> = ArrayList())

data class Image(@set:JsonProperty("imageName") var imageName: String = "", var taggedRectangles: List<TaggedRectangle> = ArrayList())

data class TaggedRectangle(var tag: String = "")
