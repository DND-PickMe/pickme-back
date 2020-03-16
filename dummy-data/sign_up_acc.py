import requests, json
from random import *

url = "https://pickme-back.ga:8083/api/accounts"
data = { "email": "@email.com", "password": "password", "nickName":"Bob", "oneLineIntroduce": "안녕하세요.!"}
headers = {'Content-Type': 'application/json; charset=utf-8'} 

names = ["김민준", "박서준", "신예준", "박도윤", "김시우",
        "임주원", "신하준", "이지호", "김준서", "서하준",
        "이준우", "최건우", "임선우", "이민재", "이지환",
        "이서연", "송서윤", "신지우", "최민서", "임지윤",
        "이수아", "김다은", "서예은", "진소윤", "이지안",
        "김다미", "Smith", "서현진", "박동현", "최광민",
        "송재익", "엄태균", "제윤태", "안현찬", "신재홍",
        "민경환", "임동훈", "홍길동", "김태정", "윤상현",
        "황규범", "최찬별", "배준완", "김용현", "박준현",
        "오치형", "진상준", "강상훈", "문성훈", "정진환"]

introducesPre = ["안녕하세요. 개발자 ", "팀원과 소통을 잘하면서 함께 발전하는" , "안녕하세요. 소프트웨어를 통해 사용자를 행복하게 만들고 싶은 백엔드 개발자 "
            , "안녕하세요! UI/UX 디자인을 하고 있는 " ,"웹 개발자로서 업계에서 성장하고 싶은 개발자 "]
introducesPost = ["입니다!", "가 되고 싶습니다.", "입니다.", "입니다!", "입니다."]
        
with requests.Session() as s:
    for i in range(50):
        data['email'] = "person" + str(i) + "@email.com"
        data['nickName'] = names[i]
        randomValue = randint(0, 4)
        data['oneLineIntroduce'] = introducesPre[randomValue] + names[i] + introducesPost[randomValue]
        _data = json.dumps(data)
        res = s.post(url, headers=headers, data=_data)
        # res.raise_for_status()
        print(res.text)
