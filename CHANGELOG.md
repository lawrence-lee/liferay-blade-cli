# Liferay Blade CLI Change Log

## 3.0.0 - _unreleased, scheduled for inclusion_

### Added
- [BLADE-190]: As a developer, I would like blade to support deploying wars to Liferay
- [BLADE-201]: Add log settings to all samples so their INFO logging can be seen
- [BLADE-211]: Create CHANGELOG.md file in github for keeping track of changes	

### Changed
- [BLADE-202]: Update Blade CLI to use JCommander
- [BLADE-207]: Upgrade all Blade Samples to set up logging	

### Fixed
- [BLADE-206]: `blade create` puts wars inside the modules folder, they should go in the wars folder
- [BLADE-208]: Restore `blade version` command and add a test for it
- [BLADE-209]: Restore `blade help` command and add a test for it
- [BLADE-210]: blade create inside of 'modules' subfolder of workspace fails to create a 'workspace' type of project

## 2.3.1 - 2017-11-21

### Changed

- [LPS-75805]: CSS Builder 2.0.2

## 2.3.0 - 2017-11-02

### Changed
- Updated Gradle Plugins Workspace
  - [LPS-73746]: Trim authentication token in case users add extra lines into their token file.
  - [LPS-73913]: Add the ability to pass the Liferay bundle authentication token password from a file by setting the liferay.workspace.bundle.token.password.file property in gradle.properties
  - [LPS-74124]: Add the ability to configure the cache directory for downloaded Liferay bundles by setting the liferay.workspace.bundle.cache.dir property in gradle.properties
  - [LPS-74818]: Fail the build if the source and destination of the downloadBundle task are the same
  - [LPS-75479]: Use Liferay 7.0.4 GA5 by default
- [Updated Liferay Gradle Plugin]
  - Updated Service Builder to 1.0.173
- [LPS-74994]: Added NPM Project Templates
- [LPS-75587]: Use latest Project Template Archetypes

[BLADE-190]: https://issues.liferay.com/browse/BLADE-190
[BLADE-201]: https://issues.liferay.com/browse/BLADE-201
[BLADE-202]: https://issues.liferay.com/browse/BLADE-202
[BLADE-206]: https://issues.liferay.com/browse/BLADE-206
[BLADE-207]: https://issues.liferay.com/browse/BLADE-207
[BLADE-208]: https://issues.liferay.com/browse/BLADE-208
[BLADE-209]: https://issues.liferay.com/browse/BLADE-209
[BLADE-210]: https://issues.liferay.com/browse/BLADE-210
[BLADE-211]: https://issues.liferay.com/browse/BLADE-211
[LPS-75805]: https://issues.liferay.com/browse/LPS-75805
[LPS-73746]: https://issues.liferay.com/browse/LPS-73746
[LPS-73913]: https://issues.liferay.com/browse/LPS-73913
[LPS-74124]: https://issues.liferay.com/browse/LPS-74124
[LPS-74818]: https://issues.liferay.com/browse/LPS-74818
[LPS-75479]: https://issues.liferay.com/browse/LPS-75479
[Updated Liferay Gradle Plugin]: https://github.com/liferay/liferay-portal/blob/master/modules/sdk/gradle-plugins/CHANGELOG.markdown#3523---2017-10-26 
[LPS-74994]: https://issues.liferay.com/browse/LPS-74994
[LPS-75587]: https://issues.liferay.com/browse/LPS-75587
