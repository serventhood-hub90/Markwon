package io.noties.markwon.app.samples;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.linkify.LinkifyPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20251226041900",
  title = "OAuth2 URL with custom scheme redirect_uri",
  description = "Test linkifying OAuth2 URLs that contain custom scheme redirect URIs",
  artifacts = {MarkwonArtifact.CORE, MarkwonArtifact.LINKIFY},
  tags = {Tag.links}
)
public class OAuth2UrlSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    // Guardian OAuth URL with custom scheme redirect_uri
    final String md = "" +
      "# OAuth2 URL Test\n\n" +
      "Guardian OAuth URL:\n\n" +
      "https://profile.theguardian.com/oauth2/aus3xgj525jYQRowl417/v1/authorize?prompt=login&page=signin&code_challenge=DsyNI_9gsRdeEgYSEy7vVc3avqVwseBfLssyWypHpmg&code_challenge_method=S256&client_id=0oa3xgktvwFBR3ORz417&scope=openid%20profile%20email%20offline_access%20id_token.profile.android_live_app%20guardian.members-data-api.read.self%20guardian.discussion-api.private-profile.read.self%20guardian.discussion-api.update.secure%20guardian.save-for-later.read.self%20guardian.save-for-later.update.self%20guardian.mobile-purchases-api.update.self%20guardian.identity-api.newsletters.read.self%20guardian.identity-api.newsletters.update.self%20guardian.my-guardian-prefs-api.read.self%20guardian.my-guardian-prefs-api.update.self%20guardian.apps-metering.update.self&redirect_uri=com.theguardian.oauth%3A%2Fauthorization%2Fcallback&response_type=code&state=27f764c7-5ce8-4d45-9795-9fbca2b07e0c&nonce=77aa678d-1f96-4795-8852-8de50925fcd4\n\n" +
      "This URL should be clickable.\n";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(LinkifyPlugin.create())
      .build();

    markwon.setMarkdown(textView, md);
  }
}
