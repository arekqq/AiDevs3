import argparse
import json

# Set up argument parser
parser = argparse.ArgumentParser(description='Convert text file to JSONL format')
parser.add_argument('--input', '-i', required=True, help='Input file path')
parser.add_argument('--output', '-o', required=True, help='Output file name')
parser.add_argument('--label', '-l', required=True, choices=['CORRECT', 'INCORRECT'], 
                    help='Classification label (CORRECT or INCORRECT)')

# Parse arguments
args = parser.parse_args()

# Open input and output files
with open(args.input, 'r') as input_file, open(args.output, 'w', encoding='utf-8') as output_file:
    # Read the file line by line
    for line in input_file:
        # Create the JSON structure for each line
        json_object = {
            "messages": [
                {"role": "system", "content": "Klasyfikuj wyniki"},
                {"role": "user", "content": line.strip()},
                {"role": "assistant", "content": args.label}
            ]
        }
        
        # Write the JSON object as a line in the JSONL file
        output_file.write(json.dumps(json_object, ensure_ascii=False) + '\n')
