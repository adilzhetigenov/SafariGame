import os

os.system("python3 ClassCleaner.py")


try:
    os.system("javac Model/Main.java")
    print("Compiled")
    os.system("java Model.Main")
except:
    print("Couldn't Compile")