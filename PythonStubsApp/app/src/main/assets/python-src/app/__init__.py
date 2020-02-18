import datetime
import glob
import json
import os
import random
import urllib.request
import socket

from rubicon.java import JavaClass, JavaInterface


def print_int_list():
    l = [random.randint(0, 99) for i in range(5)]
    print("random integers from 0 to 99 (inclusive)")
    print(">>> [random.randint(0, 99) for i in range(5)]")
    print(l)


def print_beeware_members():
    response = urllib.request.urlopen("https://api.github.com/orgs/beeware/members")
    body = response.read()
    parsed = json.loads(body)
    print(">>> parsed = json.loads(urllib.request.urlopen(...).read())")
    print(">>> [item['login'] for item in parsed]")
    print([item["login"] for item in parsed])


def print_now():
    now = datetime.datetime.now()
    print("Current time in current time zone")
    print(">>> datetime.datetime.now().isoformat()")
    print(now.isoformat())
    utcnow = datetime.datetime.utcnow()
    print("Current time in UTC")
    print(">>> datetime.datetime.utcnow().isoformat()")
    print(utcnow.isoformat())

def run_demo_code():
    print_now()
    print_int_list()
    print_beeware_members()

OnClickListener = JavaInterface("android/view/View$OnClickListener")
IPythonApp = JavaInterface("org/asheesh/beeware/pythonstubsapp/IPythonApp")

Button = JavaClass("android/widget/Button")
LinearLayout = JavaClass("android/widget/LinearLayout")

class OnClickRunDemoCode(OnClickListener):
    def onClick(self, _view):
        run_demo_code()


class Application(IPythonApp):
    #def __init__(self, java_activity_instance):
    #    super()
    #    # We need the Java activity from Android so we can pass a
    #    # `context` to UI elements, e.g., `Button`.
    #    self.java_activity_instance = java_activity_instance

    def onCreate(self):
        print("called Python onCreate method")

    def onStart(self):
        print("called Python onStart method")
        self.make_button()

    def onResume(self):
        print("called Python onResume method")

    def make_button(self):
        activity_class_name = os.environ['ACTIVITY_CLASS_NAME']
        activity_class = JavaClass(activity_class_name)
        java_activity_instance = activity_class.singletonThis

        linear_layout = LinearLayout(java_activity_instance)
        java_activity_instance.setContentView(linear_layout)
        button = Button(java_activity_instance)
        button.setText("Python made this button! Click Me")
        button.setOnClickListener(OnClickRunDemoCode())
        linear_layout.addView(button)
