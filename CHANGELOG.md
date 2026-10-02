# Changelog

## [0.4.1](https://github.com/Roboroads/G-Packets/compare/G-Packets-v0.4.0...G-Packets-v0.4.1) (2026-10-02)


### Features

* add the room entry, doorbell and room queue packets ([#104](https://github.com/Roboroads/G-Packets/issues/104)) ([900fd43](https://github.com/Roboroads/G-Packets/commit/900fd434c168eab32554a15d6f6164b688c726a4))
* add the room model, heightmap and floor plan editor packets ([#105](https://github.com/Roboroads/G-Packets/issues/105)) ([46c2db1](https://github.com/Roboroads/G-Packets/commit/46c2db13bc37bc2ac5061fae12038bb699fc12d3))
* let a changed packet skip the limit check, and drop two limits the client can break ([#103](https://github.com/Roboroads/G-Packets/issues/103)) ([470724d](https://github.com/Roboroads/G-Packets/commit/470724d8eb99910d6967d3c7c6fe1d242cd4baab))

## [0.4.0](https://github.com/Roboroads/G-Packets/compare/G-Packets-v0.3.0...G-Packets-v0.4.0) (2026-10-01)


### ⚠ BREAKING CHANGES

* rename fields that don't follow the developer-first naming rule ([#101](https://github.com/Roboroads/G-Packets/issues/101))
* read the badgesRank int the client reads after a player's isModerator ([#100](https://github.com/Roboroads/G-Packets/issues/100))
* keep chat style and activity point ids the library doesn't name ([#93](https://github.com/Roboroads/G-Packets/issues/93))
* only allow the max visitors the client offers when saving room settings ([#86](https://github.com/Roboroads/G-Packets/issues/86))

### Features

* add the room avatar status, action and expression packets ([#95](https://github.com/Roboroads/G-Packets/issues/95)) ([27988cd](https://github.com/Roboroads/G-Packets/commit/27988cd56f125f96afedb84af64d7d8f437814bf))
* keep chat style and activity point ids the library doesn't name ([#93](https://github.com/Roboroads/G-Packets/issues/93)) ([39182ea](https://github.com/Roboroads/G-Packets/commit/39182ea25e4b061981679b676a9643cc3c9e9686)), closes [#18](https://github.com/Roboroads/G-Packets/issues/18) [#88](https://github.com/Roboroads/G-Packets/issues/88) [#90](https://github.com/Roboroads/G-Packets/issues/90)
* mark the expiring catalog page image as unused ([#89](https://github.com/Roboroads/G-Packets/issues/89)) ([f36dd37](https://github.com/Roboroads/G-Packets/commit/f36dd37a27eade55e91225d8ba9751c3f438aef4))


### Bug Fixes

* keep unknown trailing bytes when replacing a message ([#102](https://github.com/Roboroads/G-Packets/issues/102)) ([9531758](https://github.com/Roboroads/G-Packets/commit/953175850ca8b1271b379798f3afec7d8acb1f66))
* only allow the max visitors the client offers when saving room settings ([#86](https://github.com/Roboroads/G-Packets/issues/86)) ([0fb74ef](https://github.com/Roboroads/G-Packets/commit/0fb74eff102ff85e42ea733aaddc3e92c0cab791))
* read the badgesRank int the client reads after a player's isModerator ([#100](https://github.com/Roboroads/G-Packets/issues/100)) ([ee5df63](https://github.com/Roboroads/G-Packets/commit/ee5df6312950506cd9160d970ddd5898ce0110bd))


### Code Refactoring

* rename fields that don't follow the developer-first naming rule ([#101](https://github.com/Roboroads/G-Packets/issues/101)) ([08f1e00](https://github.com/Roboroads/G-Packets/commit/08f1e008b8406acf1aea1683f1db9a52f4de128b))

## [0.3.0](https://github.com/Roboroads/G-Packets/compare/G-Packets-v0.2.0...G-Packets-v0.3.0) (2026-10-01)


### ⚠ BREAKING CHANGES

* use enums for an offer's activity point type and club level ([#81](https://github.com/Roboroads/G-Packets/issues/81))

### Features

* add the room settings packets ([#20](https://github.com/Roboroads/G-Packets/issues/20)) ([dc48535](https://github.com/Roboroads/G-Packets/commit/dc4853519db292455989ad6fe84b5e8c813a7c05))
* mark parameters, values and packets the client ignores ([#83](https://github.com/Roboroads/G-Packets/issues/83)) ([f85ecaf](https://github.com/Roboroads/G-Packets/commit/f85ecafd890f61a20b9845641652110bf7608c6d))
* use enums for an offer's activity point type and club level ([#81](https://github.com/Roboroads/G-Packets/issues/81)) ([4070265](https://github.com/Roboroads/G-Packets/commit/407026591d2a70f93a86c1cf18aceb3eda615622))

## [0.2.0](https://github.com/Roboroads/G-Packets/compare/G-Packets-v0.1.0...G-Packets-v0.2.0) (2026-10-01)


### ⚠ BREAKING CHANGES

* find @Intercept handler classes automatically ([#19](https://github.com/Roboroads/G-Packets/issues/19))
* describe every packet with a parameter schema ([#13](https://github.com/Roboroads/G-Packets/issues/13))

### Features

* Add `Chat` packet and `ChatBarStyle` enum for structured chat message handling ([fa4e685](https://github.com/Roboroads/G-Packets/commit/fa4e685f89b10d08ba4f9363808891c4a943ea82))
* Add `Gender` and `UserType` enums, refactor user model to use enums for improved type safety and structured packet handling. ([bd68144](https://github.com/Roboroads/G-Packets/commit/bd6814474202a508c65fa7915e69006a5a6b7019))
* add catalog index and page packets ([#9](https://github.com/Roboroads/G-Packets/issues/9)) ([f258390](https://github.com/Roboroads/G-Packets/commit/f258390ae232282933e9c11fcabee320ccd21f40))
* add PacketType, @Intercept handlers and replaceIn ([#1](https://github.com/Roboroads/G-Packets/issues/1)) ([f0d3507](https://github.com/Roboroads/G-Packets/commit/f0d3507b0b05539bf471cd18a95c9692834b436f))
* Added outgoing.Chat and incoming.Users packet ([7795b06](https://github.com/Roboroads/G-Packets/commit/7795b06acd86d8d9929d4fc40c44ec61cd4ad756))
* describe every packet with a parameter schema ([#13](https://github.com/Roboroads/G-Packets/issues/13)) ([03f5af2](https://github.com/Roboroads/G-Packets/commit/03f5af2f9f28a4b5b015acc33f0cf71b058b7f0b))
* find @Intercept handler classes automatically ([#19](https://github.com/Roboroads/G-Packets/issues/19)) ([8b7de76](https://github.com/Roboroads/G-Packets/commit/8b7de7604b18cd2ae83b0d4c831c2bcec0e373c1))
* Introduce `Direction` and `WiredMovementType` enums, add `WiredMovements` and respective subtypes for structured packet handling. ([44f307f](https://github.com/Roboroads/G-Packets/commit/44f307fdde74962fa917360d03654db91ad78e46))
* Set up GitHub Actions with Maven CI, add packet implementation test, and update dependencies for JUnit and Reflections ([1c0b71a](https://github.com/Roboroads/G-Packets/commit/1c0b71a5b1b9a8ed28cca9ebfbcec61738ccec6a))


### Bug Fixes

* serialize packets to JSON from fields ([#11](https://github.com/Roboroads/G-Packets/issues/11)) ([5482538](https://github.com/Roboroads/G-Packets/commit/54825382c64bd37833182205f324272de25c9583))
