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
RelativeLayout = JavaClass("android/widget/RelativeLayout")
RelativeLayout__LayoutParams = JavaClass("android/widget/RelativeLayout$LayoutParams")
ScrollView = JavaClass("android/widget/ScrollView")
View__MeasureSpec = JavaClass("android/view/View$MeasureSpec")

class OnClickRunDemoCode(OnClickListener):
    def onClick(self, _view):
        run_demo_code()


class Application(IPythonApp):
    # def __init__(self, java_activity_instance):
    #    super()
    #    # We need the Java activity from Android so we can pass a
    #    # `context` to UI elements, e.g., `Button`.
    #    self.java_activity_instance = java_activity_instance

    def onCreate(self):
        print("started Python onCreate method")
        self._this = JavaClass(os.environ["ACTIVITY_CLASS_NAME"]).singletonThis
        self.dynamic_layout = dynamic_layout = RelativeLayout(self._this)
        scroll_view = ScrollView(self._this)
        scroll_view.addView(dynamic_layout)
        self._this.setContentView(scroll_view)
        self.button1 = button1 = Button(self._this)
        button1.setText(
            "Kale chips food truck pop-up distillery prism. Craft beer art party copper mug shaman whatever quinoa try-hard synth meditation vexillologist mixtape readymade. Poutine microdosing keffiyeh, offal 8-bit chia twee. Salvia flexitarian coloring book sriracha meggings microdosing brunch vaporware craft beer. Fam green juice everyday carry, pitchfork forage retro health goth. Banjo messenger bag mlkshk VHS mumblecore austin single-origin coffee la croix. Whatever umami lumbersexual poutine organic marfa mustache raclette yuccie try-hard kickstarter tumblr cliche brooklyn food truck."
        )
        at_most_four_hundred = View__MeasureSpec.makeMeasureSpec(400, View__MeasureSpec.AT_MOST);
        button1.measure(at_most_four_hundred, View__MeasureSpec.UNSPECIFIED)
        print(f"button1 measuredWidth after measure(), {button1.getMeasuredWidth()}")
        self.button2 = button2 = Button(self._this)
        button2.setText("Zounds")
        button2.measure(View__MeasureSpec.UNSPECIFIED, View__MeasureSpec.UNSPECIFIED)
        print(f"button2 measuredWidth after measure(), {button2.getMeasuredWidth()}")
        dynamic_layout.addView(button1, RelativeLayout__LayoutParams(button1.getMeasuredWidth(), button1.getMeasuredHeight()))
        params = RelativeLayout__LayoutParams(button2.getMeasuredWidth(), button2.getMeasuredHeight())
        params.topMargin = 3000
        params.leftMargin = button1.getMeasuredWidth() + 70
        dynamic_layout.addView(button2, params)
        print("ended Python onCreate method")

    def onStart(self):
        print("called Python onStart method")

    def onResume(self):
        print("started Python onResume method")
        import time
        time.sleep(2)  # HACK
        params = RelativeLayout__LayoutParams(self.button2.getMeasuredWidth(),
                    self.button2.getMeasuredHeight())
        params.topMargin = 500
        params.leftMargin = self.button1.getMeasuredWidth() + 70
        self.dynamic_layout.updateViewLayout(self.button2, params)
        print("called Python onResume method")
