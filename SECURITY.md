# Security Policy

## Supported Versions

We recommend using the latest version of Markwon for the best security. The library is updated to comply with [Android security best practices](https://source.android.com/docs/security/bulletin/pixel).

| Version | Supported          | SDK Targets |
| ------- | ------------------ | ----------- |
| 4.6.x   | :white_check_mark: | API 34      |
| < 4.6.0 | :x:                | API 29      |

## Security Updates

This project follows Android security best practices as outlined in the [Google Pixel Security Bulletin](https://source.android.com/docs/security/bulletin/pixel):

- **Target SDK**: Updated to API 34 (Android 14) to comply with Google Play's target API level requirements
- **Compile SDK**: Updated to API 34
- **Minimum SDK**: Updated to API 21 (Android 5.0 Lollipop) to ensure compatibility with security patches
- **Dependencies**: All dependencies are kept up-to-date with latest stable versions that include security fixes

## Reporting a Vulnerability

If you discover a security vulnerability within Markwon, please send an email to the maintainers. All security vulnerabilities will be promptly addressed.

## Best Practices for Users

1. **Always use the latest version** of Markwon and its dependencies
2. **Keep your Android target SDK** up to date to benefit from the latest security improvements
3. **Review dependencies** regularly for known vulnerabilities
4. For image loading libraries (SVG, GIF), always use the latest versions as recommended in our documentation

## Third-Party Dependencies

When using third-party libraries with Markwon (like image loaders), please ensure you:
- Use the latest stable versions
- Monitor security advisories for these libraries
- Update dependencies promptly when security patches are released
