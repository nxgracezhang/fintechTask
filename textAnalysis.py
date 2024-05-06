#objective: answer what is the main risk factor for Microsoft

import os
import openai

# intentionally blocked the personal openai api key
openai.api_key = "###########################################"

def text_analysis(prompt):
    params = {
        "engine": "text-davinci-003",
        "prompt": prompt,
        "max_tokens": 100,
        "temperature": 0,
        "stop": "\n"
    }

    response = openai.Completion.create(**params)
    analyzed_text = response.choices[0].text.strip()
    return analyzed_text

def analyze_file(file_path, prompt):
    try:
        # Read
        with open(file_path, "r", encoding="utf-8") as file:
            text = file.read()

        # Analyze
        analyzed_text = text_analysis(prompt)
        return analyzed_text
    except Exception as e:
        print(f"An error occurred while analyzing {file_path}: {e}")
        return None

directory = "./sec-edgar-filings/MSFT/10-K"

prompt_text = "What is the main risk factor:"

if not os.path.exists(directory):
    print("Directory does not exist.")
else:
    for foldername in os.listdir(directory):
        folder_path = os.path.join(directory, foldername)
        
        # Check if the item in the directory is a directory itself
        if os.path.isdir(folder_path):
            print("Folder:", foldername)
            
            files = os.listdir(folder_path)
            print("Files in folder:", files)
            
            for filename in files:
                file_path = os.path.join(folder_path, filename)
                if os.path.isfile(file_path):
                    analyzed_text = analyze_file(file_path, prompt_text)
                    print("File:", filename)
                    print("Analyzed Text:")
                    print(analyzed_text)


