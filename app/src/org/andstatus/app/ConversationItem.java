/**
 * Copyright (C) 2015 yvolk (Yuri Volkov), http://yurivolkov.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.andstatus.app;

import android.database.Cursor;

import org.andstatus.app.data.MyDatabase.Msg;

public abstract class ConversationItem  implements Comparable<ConversationItem> {
    private long mMsgId;
    long mInReplyToMsgId = 0;
    long mCreatedDate = 0;

    /** Numeration starts from 0 **/
    int mListOrder = 0;
    /**
     * This order is reverse to the {@link #mListOrder}. 
     * First message in the conversation has it == 1.
     * The number is visible to the user.
     */
    int mHistoryOrder = 0;
    int mNReplies = 0;
    int mNParentReplies = 0;
    int mIndentLevel = 0;
    int mReplyLevel = 0;
    
    public ConversationItem() {
        super();
    }

    /**
     * The newest replies are first, "branches" look up
     */
    @Override
    public int compareTo(ConversationItem another) {
        int compared = mListOrder - another.mListOrder;
        if (compared == 0) {
            if (mCreatedDate == another.mCreatedDate) {
                if ( getMsgId() == another.getMsgId()) {
                    compared = 0;
                } else {
                    compared = (another.getMsgId() - getMsgId() > 0 ? 1 : -1);
                }
            } else {
                compared = (another.mCreatedDate - mCreatedDate > 0 ? 1 : -1);
            }
        }
        return compared;
    }

    long getMsgId() {
        return mMsgId;
    }

    void setMsgId(long mMsgId) {
        this.mMsgId = mMsgId;
    }

    protected boolean isWrongReply() {
        return false;
    }

    public boolean isLoaded() {
        return mCreatedDate > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(ConversationItem.class.isAssignableFrom(o.getClass()))) {
            return false;
        }
        final ConversationItem other = (ConversationItem) o;
        return getMsgId() == other.getMsgId();
    }

    @Override
    public int hashCode() {
        return Long.valueOf(getMsgId()).hashCode();
    }

    void copyFromWrongReply(ConversationItem aReply) {
        // This allows to place the message on a Timeline correctly
        mCreatedDate = aReply.mCreatedDate - 60000;
    }

    abstract String[] getProjection();
    
    void load(Cursor cursor) {
        mInReplyToMsgId = cursor.getLong(cursor.getColumnIndex(Msg.IN_REPLY_TO_MSG_ID));
        mCreatedDate = cursor.getLong(cursor.getColumnIndex(Msg.CREATED_DATE));
    }
    
}
