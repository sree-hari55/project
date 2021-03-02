package models.mattermost;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FileInfo {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("create_at")
    @Expose
    private Long createAt;
    @SerializedName("update_at")
    @Expose
    private Long updateAt;
    @SerializedName("delete_at")
    @Expose
    private Long deleteAt;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("extension")
    @Expose
    private String extension;
    @SerializedName("size")
    @Expose
    private Long size;
    @SerializedName("mime_type")
    @Expose
    private String mimeType;
    @SerializedName("width")
    @Expose
    private Long width;
    @SerializedName("height")
    @Expose
    private Long height;
    @SerializedName("has_preview_image")
    @Expose
    private Boolean hasPreviewImage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public FileInfo withId(String id) {
        this.id = id;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public FileInfo withUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public Long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Long createAt) {
        this.createAt = createAt;
    }

    public FileInfo withCreateAt(Long createAt) {
        this.createAt = createAt;
        return this;
    }

    public Long getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Long updateAt) {
        this.updateAt = updateAt;
    }

    public FileInfo withUpdateAt(Long updateAt) {
        this.updateAt = updateAt;
        return this;
    }

    public Long getDeleteAt() {
        return deleteAt;
    }

    public void setDeleteAt(Long deleteAt) {
        this.deleteAt = deleteAt;
    }

    public FileInfo withDeleteAt(Long deleteAt) {
        this.deleteAt = deleteAt;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FileInfo withName(String name) {
        this.name = name;
        return this;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public FileInfo withExtension(String extension) {
        this.extension = extension;
        return this;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public FileInfo withSize(Long size) {
        this.size = size;
        return this;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public FileInfo withMimeType(String mimeType) {
        this.mimeType = mimeType;
        return this;
    }

    public Long getWidth() {
        return width;
    }

    public void setWidth(Long width) {
        this.width = width;
    }

    public FileInfo withWidth(Long width) {
        this.width = width;
        return this;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public FileInfo withHeight(Long height) {
        this.height = height;
        return this;
    }

    public Boolean getHasPreviewImage() {
        return hasPreviewImage;
    }

    public void setHasPreviewImage(Boolean hasPreviewImage) {
        this.hasPreviewImage = hasPreviewImage;
    }

    public FileInfo withHasPreviewImage(Boolean hasPreviewImage) {
        this.hasPreviewImage = hasPreviewImage;
        return this;
    }
}
