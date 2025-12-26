# Linkify

<MavenBadge4 :artifact="'linkify'" />

A plugin to automatically add links to your markdown. Currently autolinking works for:
* email (`me@web.com`)
* phone numbers (`+10000000`)
* web URLS

:::warning
`Linkify` plugin is based on `android.text.util.Linkify` which can lead to significant performance 
drop due to its implementation based on regex.
:::

:::danger
Do not use `autolink` XML attribute on your `TextView` as it will remove 
all links except autolinked ones ¯\\\_(ツ)_/¯
:::

```java
final Markwon markwon = Markwon.builder(context)
        // will autolink all supported types
        .usePlugin(LinkifyPlugin.create())
        // the same as above
        .usePlugin(LinkifyPlugin.create(
                Linkify.EMAIL_ADDRESSES | Linkify.PHONE_NUMBERS | Linkify.WEB_URLS
        ))
        // only emails
        .usePlugin(LinkifyPlugin.create(Linkify.EMAIL_ADDRESSES))
        .build();
```

## OAuth and Long URLs

The LinkifyPlugin properly handles long URLs, including OAuth2 authorization URLs with complex query parameters. For example, URLs like:

```
https://example.com/oauth2/authorize?redirect_uri=com.example.app%3A%2Fcallback&state=...
```

will be correctly linkified, even when the `redirect_uri` parameter contains custom URI schemes (e.g., `com.example.app://callback`).

:::tip
For OAuth flows, ensure your redirect URIs are properly URL-encoded in the authorization URL. The LinkifyPlugin will preserve the full URL including all query parameters.
:::