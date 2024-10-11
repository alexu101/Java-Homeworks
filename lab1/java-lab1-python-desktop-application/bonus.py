import asyncio
import aiohttp
import time

async def fetch_tree(session, vertices, k):
    url = f'http://localhost:8080/java_lab1_war_exploded//bonus-servlet?vertices={vertices}&k={k}'
    start_time = time.time()
    async with session.get(url) as response:
        content = await response.text()
    elapsed_time = time.time() - start_time
    return content, elapsed_time

async def main(vertices, k, num_requests):
    print(f"Sending {num_requests} requests for graphs with {vertices} vertices and {k} spanning trees.")
    async with aiohttp.ClientSession() as session:
        tasks = [fetch_tree(session, vertices, k) for _ in range(num_requests)]
        start_time = time.time()
        responses = await asyncio.gather(*tasks)
        total_time = time.time() - start_time

        avg_time = total_time / num_requests
        individual_avg_time = sum(elapsed for _, elapsed in responses) / num_requests
        
        for i, (content, elapsed_time) in enumerate(responses):
            print(f"Response {i + 1} (Time: {elapsed_time:.2f} seconds):\n{content}\n")

        print(f'Total requests: {num_requests}')
        print(f'Total time: {total_time:.2f} seconds')
        print(f'Average total response time: {avg_time:.2f} seconds')
        print(f'Average individual response time: {individual_avg_time:.2f} seconds')

if __name__ == '__main__':
    vertices = 5
    k = 2 
    num_requests = 5 

    asyncio.run(main(vertices, k, num_requests))
