import datetime
import json
import random
import urllib.request


def print_int_list():
    l = [random.randint(0, 99) for i in range(5)]
    print("random integers from 0 to 99 (inclusive)", l)


def print_beeware_members():
    response = urllib.request.urlopen("https://api.github.com/orgs/beeware/members")
    body = response.read()
    parsed = json.loads(body)
    print([item["login"] for item in parsed])


def print_now():
    now = datetime.datetime.now()
    print("Current time in current time zone", now.isoformat())
    utcnow = datetime.datetime.utcnow()
    print("Current time in UTC", utcnow.isoformat())


def do_everything():
    print_now()
    print_int_list()
    print_beeware_members()


class Application:
    def onCreate(self):
        print("onCreate called")
        print_int_list()

    def onStart(self):
        print("onStart called")

    def onResume(self):
        print("onResume called")

    def make_button(self):
        pass

    def add_button_onclick(self, button):
        # TODO: Add this to the onclick.
        print_beeware_members()
