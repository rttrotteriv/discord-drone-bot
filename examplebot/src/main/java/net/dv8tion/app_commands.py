import requests


url = "https://discord.com/api/v8/applications/{app-id}/commands"

# This is an example CHAT_INPUT or Slash Command, with a type of 1
json = {
    "name": "blep",
    "type": 1,
    "description": "Send a random adorable animal photo",
    "options": [
        {
            "name": "animal",
            "description": "The type of animal",
            "type": 3,
            "required": True,
            "choices": [
                {
                    "name": "Dog",
                    "value": "animal_dog"
                },
                {
                    "name": "Cat",
                    "value": "animal_cat"
                },
                {
                    "name": "Penguin",
                    "value": "animal_penguin"
                }
            ]
        },
        {
            "name": "only_smol",
            "description": "Whether to show only baby animals",
            "type": 5,
            "required": False
        }
    ]
}

# For authorization, you can use either your bot token
headers = {
    "Authorization": "Bot OTIxOTM1NzU5NjA1MzY2ODI0.Yb6JlQ.WikNE78fB16-bTK2bCHkMkMkEdk"
}

# or a client credentials token for your app with the applications.commands.update scope
#headers = {
#    "Authorization": "Bearer <my_credentials_token>"
#}

r = requests.post(url, headers=headers, json=json)