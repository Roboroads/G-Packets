# Changelog

This project keeps a changelog based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/). Dates are in YYYY-MM-DD.

## Unreleased

### Added
- `PacketType<X> TYPE` on every packet, holding the header, direction and parser.
- `@Intercept` with `GPackets.init(...)` for opt-in handler registration. With no value, the packet type is inferred from the handler's first parameter.
- `Packet.replaceIn(HMessage)` for explicit, opt-in packet replacement. It keeps the original header id and marks the packet edited.

### Changed
- Packets use `TYPE` instead of the `HEADER` string constant. `PacketImplementationTest` now gates on `TYPE`.
- G-Earth dependency scope set to `provided`.
- Pinned maven-surefire-plugin 3.5.4 so the JUnit 5 tests run. The Maven default (2.17) ran none.
- Rewrote the README with correct install coordinates and the three usage options.
