from flask import Flask, request, jsonify
import ssl

app = Flask(__name__)

@app.route('/login', methods=['POST'])
def authentication():

    data = request.get_json()
    
    username = data.get('username')
    password = data.get('password')
    
    if username == "test" and password == "test":
        return jsonify({"message": "Login Successful!"}), 200
    else:
        return jsonify({"message": "Invalid credentials!"}), 401

if __name__ == '__main__':

    context = ssl.SSLContext(ssl.PROTOCOL_TLS_SERVER)
    context.load_cert_chain(certfile='server_cert.pem', keyfile='server.key')
    context.load_verify_locations(cafile='client_cert.pem')
    context.verify_mode = ssl.CERT_REQUIRED

    app.run(host='0.0.0.0', port=443, ssl_context=context)
