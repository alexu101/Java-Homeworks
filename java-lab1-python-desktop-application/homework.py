import requests

def call_request(vertices, edges):
    url = "http://localhost:8080/java_lab1_war_exploded/homework-servlet"
    params = {
        'numVertices': vertices,
        'numEdges': edges,
        'desktop': True
    }

    response = requests.get(url, params=params)

    if response.status_code == 200:
        print("Response from servlet: ")
        print(response.text)
    else:
        print(f"Error:{response.status_code}")

if __name__ =="__main__":
    call_request(5, 4)