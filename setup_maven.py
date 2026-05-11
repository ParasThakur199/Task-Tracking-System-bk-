import urllib.request
import zipfile
import os

maven_url = "https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip"
zip_path = "maven.zip"

print("Downloading Maven... Please wait.")
try:
    urllib.request.urlretrieve(maven_url, zip_path)
    print("Download complete. Extracting Maven...")
    
    with zipfile.ZipFile(zip_path, 'r') as zip_ref:
        zip_ref.extractall()
        
    os.remove(zip_path)
    print("---------------------------------------------------------")
    print("Maven setup successful!")
    print("Now, to run your Spring Boot project, use this exact command:")
    print(r".\apache-maven-3.9.6\bin\mvn.cmd spring-boot:run")
    print("---------------------------------------------------------")
except Exception as e:
    print(f"An error occurred: {e}")
