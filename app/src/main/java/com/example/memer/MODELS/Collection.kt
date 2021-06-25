package com.example.memer.MODELS

enum class FireStoreCollection(val value: String) {
    User(value = "USER_COLLECTION"),
    Post(value = "POST_COLLECTION"),
    Like(value = "LIKES"),
    Comment(value = "COMMENTS"),
    Report(value = "REPORTS"),
    Archive(value = "ARCHIVE"),
    BookMark(value = "BOOKMARK"),
    UserPublic(value = "USER_PUBLIC_COLLECTION")
}


enum class PostElement(val value: String) {
    PostId(value = "postId"),

    PostOwnerId(value = "postOwnerId"),

    PostResource(value = "postResource"),
    PostDescription(value = "postDescription"),
    PostTypeImage(value = "postTypeImage"),

    Username(value = "username"),
    UserAvatarReference(value = "userAvatarReference"),

    LikeCount(value = "likeCount"),
    CommentCount(value = "commentCount"),
    BookMarkCount(value = "bookMarkCount"),
    ReportCount(value = "reportCount"),

    CreatedAt(value = "createdAt"),
    UpdatedAt(value = "updatedAt")
}

enum class UserElement(val value: String) {
    UserId(value = "userId"),
    Username(value = "username"),
    NameOfUser(value = "nameOfUser"),

    SignInType(value = "signInType"),
    PhoneNumber(value = "phoneNumber"),
    Bio(value = "bio"),
    UserProfilePicReference(value = "userProfilePicReference"),

    UserPostCount(value = "userPostCount"),
    UserAvatarReference(value = "userAvatarReference"),
    UserGlobalLikes(value = "userGlobalLikes"),

    CreatedAt(value = "createdAt"),
    UpdatedAt(value = "updatedAt")
}

enum class BookMarkElement(val value: String) {
    BookmarkId(value = "bookmarkId"),
    PostId(value = "postId"),
    UserId(value = "userId"),

    PostOwnerId(value = "postOwnerId"),
    PostOwnerUsername(value = "postOwnerUsername"),
    PostOwnerAvatarReference(value = "postOwnerAvatarReference"),

    CreatedAt(value = "createdAt"),

}

enum class CommentElement(val value: String) {
    CommentId(value = "commentId"),
    CommentContent(value = "commentContent"),
    CommentParentId(value = "commentParentId"),

    CommentOwnerId(value = "commentOwnerId"),
    CommentOwnerUsername(value = "commentOwnerUsername"),
    CommentOwnerUserAvatar(value = "commentOwnerUserAvatar"),

    CommentPostOwnerId(value = "commentPostOwnerId"),
    CommentPostId(value = "commentPostId"),

    CommentReplyCount(value = "commentReplyCount"),
    CommentLikeCount(value = "commentLikeCount"),

    CreatedAt(value = "createdAt"),
    UpdatedAt(value = "updatedAt")
}

enum class LikeElement(val value: String) {
    LikeId(value = "likeId"),
    UserId(value = "userId"),
    Username(value = "username"),

    NameOfUser(value = "nameOfUser"),
    UserAvatarReference(value = "userAvatarReference"),
    LikeType(value = "likeType"),

    LikeTypeId(value = "likeTypeId"),
    CreatedAt(value = "createdAt"),
}

enum class ReportElement(val value: String) {
    ReportId(value = "reportId"),
    ReportType(value = "reportType"),

    PostId(value = "postId"),
    PostOwnerId(value = "postOwnerId"),
    PostOwnerUsername(value = "postOwnerUsername"),
    PostOwnerAvatarReference(value = "postOwnerAvatarReference"),

    UserId(value = "userId"),
    CreatedAt(value = "createdAt"),

    CommentId(value = "commentId"),
    CommentOwnerId(value = "commentOwnerId"),
    CommentOwnerUsername(value = "commentOwnerUsername"),
    CommentOwnerAvatarReference(value = "commentOwnerAvatarReference"),

    ReportedUserId(value = "reportedUserId"),
    ReportedUserAvatarReference(value = "reportedUserAvatarReference"),
    ReportedUserUsername(value = "reportedUserUsername"),

}

enum class ArchivedPostElement(val value: String) {
    PostId(value = "postId"),
    UserId(value = "userId"),
    CreatedAt(value = "createdAt")
}