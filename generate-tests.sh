#!/usr/bin/env bash
set -euo pipefail

INPUT_DIR="${1:-}"
OUTPUT_DIR="${2:-$(pwd)/generated-tests}"

if [[ -z "${INPUT_DIR}" ]]; then
  echo "Usage: $0 /absolute/path/to/input [/absolute/path/to/output]" >&2
  exit 2
fi

if [[ "${INPUT_DIR}" != /* ]]; then
  echo "ERROR: input path must be an absolute path: ${INPUT_DIR}" >&2
  exit 2
fi

if [[ ! -d "${INPUT_DIR}" ]]; then
  echo "ERROR: input path is not a directory: ${INPUT_DIR}" >&2
  exit 2
fi

GRADLE_CMD=""
if [[ -x "$(pwd)/gradlew" ]]; then
  GRADLE_CMD="$(pwd)/gradlew"
elif command -v gradle >/dev/null 2>&1; then
  GRADLE_CMD="gradle"
else
  echo "ERROR: gradle not found. Install Gradle or add Gradle Wrapper (./gradlew)." >&2
  exit 2
fi

"${GRADLE_CMD}" -q clean jar

java -jar "$(pwd)/build/libs/jtcg.jar" --input "${INPUT_DIR}" --output "${OUTPUT_DIR}" --overwrite

echo "Done. Generated tests under: ${OUTPUT_DIR}"
