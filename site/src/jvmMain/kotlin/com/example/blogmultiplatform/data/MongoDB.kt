package com.example.blogmultiplatform.data

import com.example.shared.Category
import com.example.blogmultiplatform.models.Constants.POSTS_PER_PAGE
import com.example.blogmultiplatform.models.Newsletter
import com.example.blogmultiplatform.models.Post
import com.example.blogmultiplatform.models.PostWithoutDetails
import com.example.blogmultiplatform.models.User
import com.example.blogmultiplatform.util.Constants.DATABASE_NAME
import com.example.blogmultiplatform.util.Constants.MAIN_POSTS_LIMIT
import com.example.blogmultiplatform.util.LoggerImpl
import com.example.blogmultiplatform.util.logError
import com.example.blogmultiplatform.util.logInfo
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Indexes.descending
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.varabyte.kobweb.api.data.add
import com.varabyte.kobweb.api.init.InitApi
import com.varabyte.kobweb.api.init.InitApiContext
import java.util.logging.FileHandler
import java.util.logging.Logger
import java.util.logging.SimpleFormatter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.types.ObjectId

@InitApi
fun initMongoDB(ctx: InitApiContext) {
    System.setProperty(
        "org.litote.mongo.test.mapping.service",
        "org.litote.kmongo.serialization.SerializationClassMappingTypeService"
    )
    ctx.data.add(MongoDB(ctx))
}

class MongoDB(private val context: InitApiContext) : MongoRepository {
    private val logger = LoggerImpl // Use singleton

    // For testing with a localhost.
//    private val client = MongoClient.create()
    // For a remote mongo database.
//    private val client = MongoClient.create(System.getenv("MONGODB_URI"))
    private val client = MongoClient.create("mongodb+srv://abhisheksfs6892:csS9PX9q1Tx76GMp@cluster0.rhaswg4.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0")

    private val database = client.getDatabase(DATABASE_NAME)
    private val userCollection = database.getCollection<User>("user")
    private val postCollection = database.getCollection<Post>("post")
    private val newsletterCollection = database.getCollection<Newsletter>("newsletter")

    override suspend fun addPost(post: Post): Boolean {
        logger.logInfo("MongoDB.kt::addPost - addPost called for post: ${post.title}")
        val result = postCollection.insertOne(post).wasAcknowledged()
        logger.logInfo("MongoDB.kt::addPost - addPost result: $result for post: ${post.title}")
        return result
    }

    override suspend fun updatePost(post: Post): Boolean {
        logger.logInfo("MongoDB.kt::updatePost - updatePost called for post: ${post._id}")
        val result = postCollection
            .updateOne(
                Filters.eq(Post::_id.name, post._id),
                mutableListOf(
                    Updates.set(Post::title.name, post.title),
                    Updates.set(Post::subtitle.name, post.subtitle),
                    Updates.set(Post::category.name, post.category),
                    Updates.set(Post::thumbnail.name, post.thumbnail),
                    Updates.set(Post::content.name, post.content),
                    Updates.set(Post::main.name, post.main),
                    Updates.set(Post::popular.name, post.popular),
                    Updates.set(Post::sponsored.name, post.sponsored)
                )
            )
            .wasAcknowledged()
        logger.logInfo("MongoDB.kt::updatePost - updatePost result: $result for post: ${post._id}")
        return result
    }

    override suspend fun readMyPosts(skip: Int, author: String): List<PostWithoutDetails> {
        logger.logInfo("MongoDB.kt::readMyPosts - readMyPosts called for author: $author, skip: $skip")
        val result = postCollection
            .withDocumentClass(PostWithoutDetails::class.java)
            .find(Filters.eq(PostWithoutDetails::author.name, author))
            .sort(descending(PostWithoutDetails::date.name))
            .skip(skip)
            .limit(POSTS_PER_PAGE)
            .toList()
        logger.logInfo("MongoDB.kt::readMyPosts - readMyPosts result count: ${result.size} for author: $author")
        return result
    }

    override suspend fun readMainPosts(): List<PostWithoutDetails> {
        logger.logInfo("MongoDB.kt::readMainPosts - readMainPosts called")
        val result = postCollection
            .withDocumentClass(PostWithoutDetails::class.java)
            .find(Filters.eq(PostWithoutDetails::main.name, true))
            .sort(descending(PostWithoutDetails::date.name))
            .limit(MAIN_POSTS_LIMIT)
            .toList()
        logger.logInfo("MongoDB.kt::readMainPosts - readMainPosts result count: ${result.size}")
        return result
    }

    override suspend fun readLatestPosts(skip: Int): List<PostWithoutDetails> {
        logger.logInfo("MongoDB.kt::readLatestPosts - readLatestPosts called, skip: $skip")
        val result = postCollection
            .withDocumentClass(PostWithoutDetails::class.java)
            .find(
                Filters.and(
                    Filters.eq(PostWithoutDetails::popular.name, false),
                    Filters.eq(PostWithoutDetails::main.name, false),
                    Filters.eq(PostWithoutDetails::sponsored.name, false)
                )
            )
            .sort(descending(PostWithoutDetails::date.name))
            .skip(skip)
            .limit(POSTS_PER_PAGE)
            .toList()
        logger.logInfo("MongoDB.kt::readLatestPosts - readLatestPosts result count: ${result.size}")
        return result
    }

    override suspend fun readSponsoredPosts(): List<PostWithoutDetails> {
        logger.logInfo("MongoDB.kt::readSponsoredPosts - readSponsoredPosts called")
        val result = postCollection
            .withDocumentClass(PostWithoutDetails::class.java)
            .find(Filters.eq(PostWithoutDetails::sponsored.name, true))
            .sort(descending(PostWithoutDetails::date.name))
            .limit(2)
            .toList()
        logger.logInfo("MongoDB.kt::readSponsoredPosts - readSponsoredPosts result count: ${result.size}")
        return result
    }

    override suspend fun readPopularPosts(skip: Int): List<PostWithoutDetails> {
        logger.logInfo("MongoDB.kt::readPopularPosts - readPopularPosts called, skip: $skip")
        val result = postCollection
            .withDocumentClass(PostWithoutDetails::class.java)
            .find(Filters.eq(PostWithoutDetails::popular.name, true))
            .sort(descending(PostWithoutDetails::date.name))
            .skip(skip)
            .limit(POSTS_PER_PAGE)
            .toList()
        logger.logInfo("MongoDB.kt::readPopularPosts - readPopularPosts result count: ${result.size}")
        return result
    }

    override suspend fun deleteSelectedPosts(ids: List<String>): Boolean {
        logger.logInfo("MongoDB.kt::deleteSelectedPosts - deleteSelectedPosts called for ids: $ids")
        val result = postCollection
            .deleteMany(Filters.`in`(Post::_id.name, ids))
            .wasAcknowledged()
        logger.logInfo("MongoDB.kt::deleteSelectedPosts - deleteSelectedPosts result: $result for ids: $ids")
        return result
    }

    override suspend fun searchPostsByTittle(query: String, skip: Int): List<PostWithoutDetails> {
        logger.logInfo("MongoDB.kt::searchPostsByTittle - searchPostsByTittle called, query: $query, skip: $skip")
        val regexQuery = query.toRegex(RegexOption.IGNORE_CASE)
        val result = postCollection
            .withDocumentClass(PostWithoutDetails::class.java)
            .find(Filters.regex(PostWithoutDetails::title.name, regexQuery.pattern))
            .sort(descending(PostWithoutDetails::date.name))
            .skip(skip)
            .limit(POSTS_PER_PAGE)
            .toList()
        logger.logInfo("MongoDB.kt::searchPostsByTittle - searchPostsByTittle result count: ${result.size}")
        return result
    }

    override suspend fun searchPostsByCategory(
        category: Category,
        skip: Int
    ): List<PostWithoutDetails> {
        logger.logInfo("MongoDB.kt::searchPostsByCategory - searchPostsByCategory called, category: $category, skip: $skip")
        val result = postCollection
            .withDocumentClass(PostWithoutDetails::class.java)
            .find(Filters.eq(PostWithoutDetails::category.name, category))
            .sort(descending(PostWithoutDetails::date.name))
            .skip(skip)
            .limit(POSTS_PER_PAGE)
            .toList()
        logger.logInfo("MongoDB.kt::searchPostsByCategory - searchPostsByCategory result count: ${result.size}")
        return result
    }

    override suspend fun readSelectedPost(id: String): Post {
        logger.logInfo("MongoDB.kt::readSelectedPost - readSelectedPost called for id: $id")
        val result = postCollection.find(Filters.eq(Post::_id.name, id)).toList().first()
        logger.logInfo("MongoDB.kt::readSelectedPost - readSelectedPost result: ${result._id}")
        return result
    }

    override suspend fun checkUserExistence(user: User): User? {
        val timestamp = java.time.LocalDateTime.now().withSecond(0).withNano(0)
        logger.logInfo("[$timestamp] MongoDB.kt::checkUserExistence - checkUserExistence called for username: ${user.username}, password: ${user.password}, role: ${user.role}")
        return try {
            val doc = userCollection
                .withDocumentClass(org.bson.Document::class.java)
                .find(
                    Filters.and(
                        Filters.eq("username", user.username),
                        Filters.eq("password", user.password),
                        Filters.eq("role", user.role),
                    )
                ).firstOrNull()
            if (doc != null) {
                val id = try { doc.getObjectId("_id").toHexString() } catch (e: Exception) {
                    logger.logError("[$timestamp] MongoDB.kt::checkUserExistence - Error decoding _id: ${e.message}")
                    ""
                }
                val username = doc.getString("username")
                val password = doc.getString("password")
                val role = doc.getString("role")
                val userResult = User(_id = id, username = username, password = password, role = role)
                logger.logInfo("[$timestamp] MongoDB.kt::checkUserExistence - checkUserExistence result: ${userResult.username}, password: ${userResult.password}")
                userResult
            } else {
                logger.logInfo("[$timestamp] MongoDB.kt::checkUserExistence - No user found for username: ${user.username}")
                null
            }
        } catch (e: Exception) {
            logger.logError("[$timestamp] MongoDB.kt::checkUserExistence - checkUserExistence error: ${e.message}")
            null
        }
    }

    override suspend fun checkUserId(id: String): Boolean {
        logger.logInfo("MongoDB.kt::checkUserId - checkUserId called for id: $id")
        return try {
            val documentCount = userCollection.countDocuments(Filters.eq(User::_id.name, ObjectId(id)))
            logger.logInfo("MongoDB.kt::checkUserId - Checked user ID: $id, found: $documentCount")
            documentCount > 0
        } catch (e: Exception) {
            logger.logError("MongoDB.kt::checkUserId - checkUserId error: ${e.message}")
            false
        }
    }

    override suspend fun subscribe(newsletter: Newsletter): String {
        logger.logInfo("MongoDB.kt::subscribe - subscribe called for email: ${newsletter.email}")
        val result = newsletterCollection
            .find(Filters.eq(Newsletter::email.name, newsletter.email))
            .toList()
        logger.logInfo("MongoDB.kt::subscribe - subscribe find result count: ${result.size} for email: ${newsletter.email}")
        return if (result.isNotEmpty()) {
            logger.logInfo("MongoDB.kt::subscribe - Already subscribed: ${newsletter.email}")
            "You're already subscribed."
        } else {
            val newEmail = newsletterCollection
                .insertOne(newsletter)
                .wasAcknowledged()
            logger.logInfo("MongoDB.kt::subscribe - subscribe insert result: $newEmail for email: ${newsletter.email}")
            if (newEmail) "Successfully Subscribed!"
            else "Something went wrong. Please try again later."
        }
    }
}