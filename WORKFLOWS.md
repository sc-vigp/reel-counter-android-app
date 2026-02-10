# GitHub Actions Workflows Documentation

This document explains the automated CI/CD workflows configured for the Reel Counter Android App.

## Overview

The project includes two main workflows:
1. **Build and Release APK** - Continuous integration workflow for automated builds
2. **Create Release** - Release workflow for creating versioned releases

## Build and Release APK Workflow

**File**: `.github/workflows/build-apk.yml`

### Triggers

This workflow runs automatically on:
- **Push events** to `main` or `develop` branches
- **Pull request events** targeting `main` or `develop` branches
- **Manual dispatch** via GitHub Actions UI

### Workflow Steps

1. **Checkout Code**: Fetches the repository code
2. **Set up JDK 17**: Configures Java Development Kit 17 (Temurin distribution)
3. **Cache Gradle**: Caches Gradle dependencies for faster builds
4. **Grant Permissions**: Ensures gradlew is executable
5. **Run Tests**: Executes unit tests with `./gradlew test`
6. **Build Debug APK**: Builds debug variant with `./gradlew assembleDebug`
7. **Build Release APK**: Builds release variant with `./gradlew assembleRelease`
8. **Version Information**: Extracts version from build.gradle.kts and generates build metadata
9. **Rename APKs**: Renames APK files with version and commit hash
10. **Upload Artifacts**: Uploads APKs as workflow artifacts
11. **Create Summary**: Generates a build summary with version info

### Artifacts

The workflow produces the following artifacts:

| Artifact | Retention | Description |
|----------|-----------|-------------|
| `ReelCounter-debug-v{version}-{commit}` | 30 days | Debug APK with debugging enabled |
| `ReelCounter-release-v{version}-{commit}` | 90 days | Release APK (unsigned) |
| `test-results` | 7 days | Unit test results |

### APK Naming Format

- **Debug**: `ReelCounter-{version}-debug-{commit}.apk`
  - Example: `ReelCounter-1.0-debug-abc1234.apk`
- **Release**: `ReelCounter-{version}-release-{commit}.apk`
  - Example: `ReelCounter-1.0-release-abc1234.apk`

### Downloading APKs

1. Navigate to the repository's Actions tab
2. Click on a successful workflow run
3. Scroll to the "Artifacts" section at the bottom
4. Click on the artifact name to download

## Create Release Workflow

**File**: `.github/workflows/release.yml`

### Triggers

This workflow runs when you push a version tag following semantic versioning:
- Pattern: `v*.*.*` (e.g., `v1.0.0`, `v2.1.3`, `v0.1.0`)

### Creating a Release

```bash
# Step 1: Ensure all changes are committed
git add .
git commit -m "Prepare release v1.0.0"

# Step 2: Create a version tag
git tag v1.0.0

# Step 3: Push the tag to GitHub
git push origin v1.0.0

# The workflow will automatically trigger and create a release
```

### Workflow Steps

1. **Checkout Code**: Fetches the repository code
2. **Set up JDK 17**: Configures Java Development Kit 17
3. **Extract Version**: Extracts version number from the tag
4. **Run Tests**: Executes unit tests
5. **Build APKs**: Builds both release and debug APKs
6. **Rename APKs**: Renames files with version number
7. **Generate Release Notes**: Creates formatted release notes
8. **Create GitHub Release**: Creates a release with attached APK files
9. **Upload Artifacts**: Saves artifacts for long-term retention (365 days)

### Release Assets

Each release includes:
- `ReelCounter-{version}-release.apk` - Release APK (unsigned)
- `ReelCounter-{version}-debug.apk` - Debug APK
- Automatically generated release notes
- Commit history since the last release

### Accessing Releases

1. Navigate to the repository's Releases page
2. Find the desired version
3. Download APK files from the "Assets" section
4. Read the release notes for changes and installation instructions

## Semantic Versioning

This project follows [Semantic Versioning 2.0.0](https://semver.org/):

```
v{MAJOR}.{MINOR}.{PATCH}
```

- **MAJOR**: Breaking changes, incompatible API changes
- **MINOR**: New features, backwards-compatible functionality
- **PATCH**: Bug fixes, backwards-compatible fixes

### Version Examples

- `v1.0.0` - First stable release
- `v1.1.0` - Added new feature (e.g., platform selection)
- `v1.1.1` - Bug fix for version 1.1.0
- `v2.0.0` - Major update with breaking changes

## Installation Instructions for End Users

### From GitHub Actions Artifacts

1. Go to [Actions](https://github.com/sc-vigp/reel-counter-android-app/actions)
2. Select the "Build and Release APK" workflow
3. Click on the latest successful run
4. Download the desired APK from "Artifacts"
5. Extract the ZIP file (artifacts are automatically zipped)
6. Transfer the APK to your Android device

### From GitHub Releases

1. Go to [Releases](https://github.com/sc-vigp/reel-counter-android-app/releases)
2. Select the desired version
3. Download the APK from "Assets"
4. Transfer to your Android device

### Installing on Android Device

1. **Enable Unknown Sources**:
   - Open Settings → Security
   - Enable "Install from Unknown Sources" or "Allow from this source"

2. **Install APK**:
   - Open the downloaded APK file
   - Tap "Install"
   - Wait for installation to complete

3. **Launch App**:
   - Tap "Open" or find "Reel Counter" in your app drawer

## Workflow Maintenance

### Updating Workflow Triggers

To modify when workflows run, edit the `on:` section in the workflow file:

```yaml
on:
  push:
    branches:
      - main
      - develop
      - feature/*  # Add more branches
  pull_request:
    branches:
      - main
```

### Modifying Build Steps

To add or modify build steps:
1. Edit `.github/workflows/build-apk.yml` or `.github/workflows/release.yml`
2. Add new steps under the `steps:` section
3. Commit and push changes
4. The workflow will use the updated configuration on next run

### Changing Retention Periods

Modify the `retention-days` parameter in upload steps:

```yaml
- name: Upload debug APK
  uses: actions/upload-artifact@v4
  with:
    retention-days: 30  # Change this value
```

## Troubleshooting

### Workflow Fails with Build Error

1. Check the workflow logs for error messages
2. Reproduce the build locally: `./gradlew assembleDebug`
3. Fix the build issues and push changes
4. The workflow will run again automatically

### APK Not Created

1. Verify the workflow completed successfully (green checkmark)
2. Check that all build steps passed
3. Look in the "Artifacts" section at the bottom of the workflow run

### Release Not Created

1. Verify the tag follows the `v*.*.*` pattern
2. Check that the tag was pushed to GitHub: `git push origin v1.0.0`
3. Ensure the workflow has `contents: write` permission

### Version Extraction Fails

1. Ensure `versionName` and `versionCode` exist in `app/build.gradle.kts`
2. Verify the format: `versionName = "1.0"` (with quotes)
3. Check workflow logs for sed command output

## Best Practices

1. **Test Before Tagging**: Always test your code before creating a release tag
2. **Meaningful Version Numbers**: Use semantic versioning appropriately
3. **Review Artifacts**: Download and test APKs before distributing
4. **Update Release Notes**: Keep release notes informative and user-friendly
5. **Monitor Workflow**: Check workflow status after pushing to catch issues early

## Additional Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Semantic Versioning Specification](https://semver.org/)
- [Android Build Configuration](https://developer.android.com/studio/build)
- [Gradle Documentation](https://docs.gradle.org/)
