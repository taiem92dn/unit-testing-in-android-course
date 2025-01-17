package com.techyourchance.testdrivendevelopment.exercise8.contacts;

import java.util.Objects;

public class Contact {

    private final String mId;
    private final String mFullName;
    private final String mImageUrl;

    public Contact(String id, String fullName, String imageUrl) {
        mId = id;
        mFullName = fullName;
        mImageUrl = imageUrl;
    }

    public String getId() {
        return mId;
    }

    public String getFullName() {
        return mFullName;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contact contact = (Contact) o;

        if (!Objects.equals(mId, contact.mId)) return false;
        if (!Objects.equals(mFullName, contact.mFullName))
            return false;
        return Objects.equals(mImageUrl, contact.mImageUrl);
    }

    @Override
    public int hashCode() {
        int result = mId != null ? mId.hashCode() : 0;
        result = 31 * result + (mFullName != null ? mFullName.hashCode() : 0);
        result = 31 * result + (mImageUrl != null ? mImageUrl.hashCode() : 0);
        return result;
    }
}
