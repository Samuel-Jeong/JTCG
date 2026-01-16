#!/usr/bin/env bash
set -euo pipefail

INPUT_DIR=""
OUTPUT_DIR=""
CLASSPATH_ARG=""

# Positional + option parsing
#
# Supported:
#   ./generate-tests.sh <input> [output] [--classpath <cp>]
#
# Backward compatible:
#   ./generate-tests.sh <input> [output] [classpath]
POSITIONAL=()
while [[ $# -gt 0 ]]; do
  case "$1" in
    --classpath)
      shift
      if [[ $# -le 0 ]]; then
        echo "ERROR: --classpath requires a value" >&2
        exit 2
      fi
      CLASSPATH_ARG="$1"
      shift
      ;;
    -h|--help)
      echo "Usage: $0 /absolute/path/to/input [/absolute/path/to/output] [--classpath <cp>]" >&2
      exit 0
      ;;
    --*)
      echo "ERROR: unknown option: $1" >&2
      echo "Usage: $0 /absolute/path/to/input [/absolute/path/to/output] [--classpath <cp>]" >&2
      exit 2
      ;;
    *)
      POSITIONAL+=("$1")
      shift
      ;;
  esac
done

INPUT_DIR="${POSITIONAL[0]:-}"
OUTPUT_DIR="${POSITIONAL[1]:-$(pwd)/generated-tests}"

# Backward compatibility: if 3rd positional exists and --classpath wasn't provided, treat it as classpath.
if [[ -z "${CLASSPATH_ARG}" && -n "${POSITIONAL[2]:-}" ]]; then
  CLASSPATH_ARG="${POSITIONAL[2]}"
fi

if [[ -z "${INPUT_DIR}" ]]; then
  echo "Usage: $0 /absolute/path/to/input [/absolute/path/to/output] [--classpath <cp>]" >&2
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

JAVA_ARGS=(--input "${INPUT_DIR}" --output "${OUTPUT_DIR}" --overwrite)
if [[ -n "${CLASSPATH_ARG}" ]]; then
  JAVA_ARGS+=("--classpath" "${CLASSPATH_ARG}")
fi

java -jar "$(pwd)/build/libs/jtcg.jar" "${JAVA_ARGS[@]}"

echo "Done. Generated tests under: ${OUTPUT_DIR}"
