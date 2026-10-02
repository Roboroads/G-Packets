#!/usr/bin/env bash
# Builds the banner from banner-source.svg, the only file to edit:
# - banner.svg: the text converted to shapes, so it looks the same on machines without these fonts
# - banner.png: 1280x640, for the repository's social preview
# Needs Inkscape 1.x and the fonts the source names: Noto Sans (with its Black weight) and Fira Code.
set -euo pipefail
cd "$(dirname "$0")"

for font in "Noto Sans" "Fira Code"; do
    fc-list -q "$font" || { echo "Missing font: $font" >&2; exit 1; }
done

inkscape banner-source.svg --export-text-to-path --export-plain-svg --export-filename=banner.svg
inkscape banner-source.svg --export-type=png --export-filename=banner.png --export-width=1280 --export-height=640
