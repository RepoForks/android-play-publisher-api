/*
 * Copyright 2014 Google Inc. All rights reserved.
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

package com.google.play.developerapi.samples;

import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.FileContent;
import com.google.api.services.androidpublisher.AndroidPublisher;
import com.google.api.services.androidpublisher.AndroidPublisher.Edits;
import com.google.api.services.androidpublisher.AndroidPublisher.Edits.Apklistings;
import com.google.api.services.androidpublisher.AndroidPublisher.Edits.Apks.Upload;
import com.google.api.services.androidpublisher.AndroidPublisher.Edits.Commit;
import com.google.api.services.androidpublisher.AndroidPublisher.Edits.Insert;
import com.google.api.services.androidpublisher.AndroidPublisher.Edits.Tracks.Update;
import com.google.api.services.androidpublisher.model.Apk;
import com.google.api.services.androidpublisher.model.ApkListing;
import com.google.api.services.androidpublisher.model.AppEdit;
import com.google.api.services.androidpublisher.model.Track;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Uploads an apk to the alpha track and updates recent changes description.
 */
public class UploadApkWithListing {

    private static final Log log = LogFactory.getLog(UploadApkWithListing.class);

    private static final String APK_LISTING_RECENT_CHANGES_TEXT = "Apk recent changes en-US";

    /**
     * Track for uploading the apk, can be 'alpha', beta', 'production' or
     * 'rollout'.
     */
    private static final String TRACK_BETA = "beta";

    public static void main(String[] args) throws Exception {
        ApplicationConfig cfg = ApplicationConfig.parseArgs(args);
        if (cfg == null) {
            return;
        }

        // Create the API service.
        AndroidPublisher service = AndroidPublisherHelper.init(cfg.appName, cfg.account, cfg.secrets);
        final Edits edits = service.edits();

        // Create a new edit to make changes.
        Insert editRequest = edits
                .insert(cfg.appId, null /** no content */);
        AppEdit edit = editRequest.execute();
        final String editId = edit.getId();
        log.info(String.format("Created edit with id: %s", editId));

        // Upload new apk to developer console
        final String apkPath = UploadApkWithListing.class
                .getResource(cfg.apkFilePath)
                .toURI().getPath();
        final AbstractInputStreamContent apkFile =
                new FileContent(AndroidPublisherHelper.MIME_TYPE_APK, new File(apkPath));
        Upload uploadRequest = edits
                .apks()
                .upload(cfg.appId, editId, apkFile);
        Apk apk = uploadRequest.execute();
        log.info(String.format("Version code %d has been uploaded",
                apk.getVersionCode()));

        // Assign apk to beta track.
        List<Integer> apkVersionCodes = new ArrayList<>();
        apkVersionCodes.add(apk.getVersionCode());
        Update updateTrackRequest = edits
                .tracks()
                .update(cfg.appId, editId, TRACK_BETA, new Track().setVersionCodes(apkVersionCodes));
        Track updatedTrack = updateTrackRequest.execute();
        log.info(String.format("Track %s has been updated.", updatedTrack.getTrack()));

        // Update recent changes field in apk listing.
        final ApkListing newApkListing = new ApkListing();
        newApkListing.setRecentChanges(APK_LISTING_RECENT_CHANGES_TEXT);

        Apklistings.Update
                updateRecentChangesRequest = edits
                .apklistings()
                .update(cfg.appId, editId, apk.getVersionCode(), Locale.US.toString(), newApkListing);
        updateRecentChangesRequest.execute();
        log.info("Recent changes has been updated.");

        // Commit changes for edit.
        Commit commitRequest = edits.commit(cfg.appId, editId);
        AppEdit appEdit = commitRequest.execute();
        log.info(String.format("App edit with id %s has been comitted", appEdit.getId()));
    }
}
