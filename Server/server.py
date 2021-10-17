from flask import Flask
from flask_restful import reqparse, abort, Api, Resource

app = Flask(__name__)


@app.route("/")
def home():
    return "Hello, World!"


@app.route("/uploadFiles", methods=['POST'])
def run():
    print("Starting processing ...")
    fileData = reqparse.request.files
    file = list(fileData.keys())[0]
    fileStorage = list(fileData.values())[0]
    print(file)
    print(fileStorage)
    fileStorage.save(file)
    print("Finished request....!!")
    return ""

if __name__ == "__main__":
    app.run(host='0.0.0.0', debug=True)
