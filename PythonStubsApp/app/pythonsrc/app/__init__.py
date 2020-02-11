import datetime
import glob
import json
import os
import random
import urllib.request
import socket
import ssl

from rubicon.java import JavaInterface


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

class Application(IPythonApp):
    def onCreate(self):
        print("called Python onCreate method")

    def onStart(self):
        print("called Python onStart method")
        do_everything()

    def onResume(self):
        print("called Python onResume method")

    def make_button(self):
        pass

    def add_button_onclick(self, button):
        # TODO: Add this to the onclick.
        print_beeware_members()
