#!/bin/bash

echo "Cleaning old results..."
rm -rf results/* snapshots/* boards/*  # ✅ Remove all old files

echo "Compiling Java..."
javac -d bin src/game/*.java src/players/*.java
if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    exit 1
fi

echo "Generating boards..."
mkdir -p boards snapshots results  # ✅ Ensure directories exist

# ✅ Define board sizes to test (adjust as needed)
BOARD_SIZES=(10 25 50)

# ✅ Generate multiple sets of boards
for size in "${BOARD_SIZES[@]}"; do
    for i in {1..5}; do  # 5 boards per size
        java -cp bin game.InstanceGenerator "boards/board_${size}x${size}_$i.dat" $size
    done
done

echo "Running tests..."
> results/TotalScores.txt  # ✅ Ensure fresh scores

while IFS= read -r studentID; do
    totalPercentage=0
    playerLogFile="results/Player${studentID}.log"

    # ✅ Ensure player's log starts fresh
    > "$playerLogFile"

    for size in "${BOARD_SIZES[@]}"; do
        for i in {1..5}; do
            boardFile="boards/board_${size}x${size}_$i.dat"
            echo "Testing $studentID on $boardFile..."

            output=$(java -cp bin game.Tester "$boardFile" "$studentID")
            score=$(echo "$output" | tail -n 1 | awk '{print $2}')  # Extract score
            maxScore=$((size * size))  # ✅ Compute max possible score
            percentage=$(awk "BEGIN {printf \"%.2f\", 100 * $score / $maxScore}")  # Compute percentage

            # ✅ Log game details: board size, game number, raw score, percentage
            echo "$size x $size - Game $i: $score ($percentage%)" >> "$playerLogFile"

            totalPercentage=$(awk "BEGIN {print $totalPercentage + $percentage}")
        done
    done

    # ✅ Compute and log final **average percentage score** per student
    totalGames=$(( ${#BOARD_SIZES[@]} * 5 ))  # Total games played
    avgPercentage=$(awk "BEGIN {printf \"%.2f\", $totalPercentage / $totalGames}")
    echo "$studentID $avgPercentage%" >> results/TotalScores.txt
done < students.txt

echo "Done! Check 'results/TotalScores.txt' and 'snapshots/' for full gameplay."
