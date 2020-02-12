import datetime
import glob
import json
import os
import random
import urllib.request
import socket
import ssl

from rubicon.java import JavaClass, JavaInterface


def print_int_list():
    l = [random.randint(0, 99) for i in range(5)]
    print("random integers from 0 to 99 (inclusive)")
    print(">>> [random.randint(0, 99) for i in range(5)]")
    print(l)


def print_beeware_members(context):
    response = urllib.request.urlopen("https://api.github.com/orgs/beeware/members", context=context)
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

def make_ssl_context():
    if os.path.exists('/etc/security/cacerts'):
        # Running on Android
        print("Using Android-specific SSL context")
        context = ssl.SSLContext(ssl.PROTOCOL_TLS_CLIENT)
        bundle_contents = ''
        for filename in glob.glob('/etc/security/cacerts/*'):
            with open(filename) as fd:
                s = fd.read()
                if 'END CERTIFICATE' in s:
                    lines = s.split('\n')
                    line_end_certificate = [i for i, line in enumerate(lines) if 'END CERTIFICATE' in line][0]
                    bundle_contents += '\n'.join(lines[0:line_end_certificate+1]) + '\n'
        bundle_path = os.environ['TMPDIR'] + '/bundle'
        with open(bundle_path, 'w') as fd:
            fd.write(bundle_contents)
        context.load_verify_locations(bundle_path)
        return context
    return ssl.create_default_context()

def do_everything():
    context = make_ssl_context()
    print_now()
    print_int_list()
    print_beeware_members(context)

IPythonApp = JavaInterface("org/asheesh/beeware/pythonstubsapp/IPythonApp")

Button = JavaClass("android/widget/Button")
LinearLayout = JavaClass("android/widget/LinearLayout")

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
        do_everything()

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
        linear_layout.addView(button)

    def add_button_onclick(self, button):
        # TODO: Add this to the onclick.
        print_beeware_members()
