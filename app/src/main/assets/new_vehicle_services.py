import time
import sys
import Queue
import threading
import rvi_ws
import json
import random
import dbus
import dbus.mainloop.glib
import websocket
import os
import gobject
import traceback
import ssl
#from pdbx import Rpdb

#rpdb = Rpdb(16001)
#rpdb.set_trace()

GPS_ENABLE = False
GPS_TICK_RATE = 10

lock = threading.Lock()

host = "ws://localhost:9008"
to_reg = {}

android_sub_cb = []

dbus.mainloop.glib.threads_init()
main_loop = dbus.mainloop.glib.DBusGMainLoop(set_as_default=True)
bus = dbus.SessionBus(mainloop = main_loop)
media_player = dbus.Interface(bus.get_object("org.genivi.mediamanager.Player_", "/"), "org.genivi.mediamanager.Player")
media_browser = dbus.Interface(bus.get_object("org.genivi.mediamanager.Browser_", "/"), "org.genivi.mediamanager.Browser")
media_indexer = dbus.Interface(bus.get_object("org.genivi.mediamanager.Indexer_", "/"), "org.genivi.mediamanager.Indexer")

class Song:
    def __init__(self, dbusdict):
        self.sampleRate = massage_type(dbusdict.get("SampleRate"))
        self.bitrate = massage_type(dbusdict.get("Bitrate"))
        self.size = massage_type(dbusdict.get("Size"))
        self.trackNumber = massage_type(dbusdict.get("TrackNumber"))
        self.artist = massage_type(dbusdict.get("Artist"))
        self.type = massage_type(dbusdict.get("Type"))
        self.displayName = massage_type(dbusdict.get("DisplayName"))
        self.duration = massage_type(dbusdict.get("Duration"))
        self.uri = massage_type(dbusdict.get("URI"))
        self.parent = massage_type(dbusdict.get("Parent"))
        self.artists = massage_type(dbusdict.get("Artists")) # dbus.Array type
        self.mimeType = massage_type(dbusdict.get("MIMEType"))
        self.path = massage_type(dbusdict.get("Path"))
        self.album = massage_type(dbusdict.get("Album"))
        self.genre = massage_type(dbusdict.get("Genre"))
        self.typeEx = massage_type(dbusdict.get("TypeEx"))
        # self.artURL = massage_type(dbusdict.get("AlbumArtURL"))

class MediaContainer:
    def __init__(self, dbusdict):
        # self.mimeType = dbusdict.get("MIMEType")[1]
        self.displayName = dbusdict.get("DisplayName")[1]
        self.parent = dbusdict.get("Parent")[1]
        self.searchable = dbusdict.get("Searchable")[1]
        # self.uri = dbusdict.get("URI")[1]
        self.path = dbusdict.get("Path")[1]
        self.typeEx = dbusdict.get("TypeEx")[1]
        self.type = dbusdict.get("Type")[1]
        self.childCount = dbusdict.get("ChildCount")[1]

def send_to_android(list_target, params):
    payload = {}
    lock.acquire()
    try:
        ws = websocket.create_connection(host, timeout=5)

        payload["json-rpc"] = "2.0"
        payload["id"] = str(time.time())
        payload["method"] = "message"
        for target in list_target:
            payload["params"] = {
                                "service_name" : target,
                                "timeout" : int(time.time()) + 60,
                                "parameters" : params
            }
        
        ws.send(json.dumps(payload))
        print("INFO: Sent message - {}".format(json.dumps(payload)))
        ws.close()

    except:
        print("ERROR: Could not send message ", payload)

    lock.release()

def massage_type(param):
    if param is not None: param = param[1]
    if type(param) == dbus.Boolean:
        return bool(param)
    if type(param) in [dbus.Int16, dbus.Int32, dbus.Byte, dbus.UInt16]: 
        return int(param)
    if type(param) in [dbus.Int64, dbus.UInt32, dbus.UInt64]:
        return long(param)
    if type(param) in [dbus.Double]:
        return float(param)
    if type(param) in [dbus.ObjectPath, dbus.Signature, dbus.UTF8String]:
        return str(param)
    if type(param) in [dbus.String]:
        return unicode(param)
    #if type(param) in [dbus.Array]:
    #    return ",".join([unicode(x) for x in param])
    return json.dumps(param)

def mediacontrol_handler(parameters):
    print("INFO: mediacontrol invoked")

    try:
        target = parameters["target"]

        if target == "SUBSCRIBE":
            print("INFO: SUBSCRIBE INVOKED")
            try:
                android_sub_cb.append(str(parameters["requestedValue"]))
                print("Callback is %s" % android_sub_cb ) 
            except Exception as e:
                print("ERROR: ", e)

        if target == "GETINTERFACEVERSION":
            print("INFO: GETINTERFACEVERSION INVOKED")
            try:
                value = media_player.getInterfaceVersion()
                send_to_android( android_sub_cb, 
                    {
                        "target": "GETINTERFACEVERSION",
                        "value": ".".join(str(i) for i in value)
                    } 
                )
            except Exception as e:
                print("ERROR: ", e)

        if target == "GETMUTEATTRIBUTE":
            print("INFO: GETMUTEATTRIBUTE INVOKED")
            try:
                value = media_player.getMuteAttribute()
                send_to_android( android_sub_cb, 
                    {
                        "target": "GETMUTEATTRIBUTE",
                        "value": int(value)
                    } 
                )
            except Exception as e:
                print("ERROR: ", e)

        if target == "SETMUTEATTRIBUTE":
            print("INFO: SETMUTEATTRIBUTE INVOKED")
            try:
                requestedValue = int(parameters["requestedValue"])
                setValue = media_player.setMuteAttribute(requestedValue)
                send_to_android( android_sub_cb, 
                    {
                        "target": "SETMUTEATTRIBUTE",
                        "setValue": int(setValue)
                    } 
                )
            except Exception as e:
                print("ERROR: ", e)

        if target == "GETSHUFFLEATTRIBUTE":
            print("INFO: GETSHUFFLEATTRIBUTE INVOKED")
            try:
                value = media_player.getShuffleAttribute()
                send_to_android( android_sub_cb, 
                    {
                        "target": "GETSHUFFLEATTRIBUTE",
                        "value": int(value)
                    } 
                )
            except Exception as e:
                print("ERROR: ", e)

        if target == "SETSHUFFLEATTRIBUTE":
            print("INFO: SETSHUFFLEATTRIBUTE INVOKED")
            try:
                requestedValue = int(parameters["requestedValue"])
                setValue = media_player.setShuffleAttribute(requestedValue)
                send_to_android( android_sub_cb, 
                    {
                        "target": "SETSHUFFLEATTRIBUTE",
                        "setValue": int(setValue)
                    } 
                )
            except Exception as e:
                print("ERROR: ", e)

        if target == "GETREPEATATTRIBUTE":
            print("INFO: GETREPEATATTRIBUTE INVOKED")
            try:
                value = media_player.getRepeatAttribute()
                send_to_android( android_sub_cb, 
                    {
                        "target": "GETREPEATATTRIBUTE",
                        "value": int(value)
                    } 
                )
            except Exception as e:
                print("ERROR: ", e)

        if target == "SETREPEATATTRIBUTE":
            print("INFO: SETREPEATATTRIBUTE INVOKED")
            try:
                requestedValue = int(parameters["requestedValue"])
                setValue = media_player.setRepeatAttribute(requestedValue)
                send_to_android( android_sub_cb, 
                    {
                        "target": "SETREPEATATTRIBUTE",
                        "setValue": int(setValue)
                    } 
                )
            except Exception as e:
                print("ERROR: ", e)

        if target == "GETRATEATTRIBUTE":
            print("INFO: GETRATEATTRIBUTE INVOKED")
            try:
                value = media_player.getRateAttribute()
                send_to_android( android_sub_cb, 
                    {
                        "target": "GETRATEATTRIBUTE",
                        "value": float(value)
                    } 
                )
            except Exception as e:
                print("ERROR: ", e)

        if target == "SETRATEATTRIBUTE":
            print("INFO: SETRATEATTRIBUTE INVOKED")
            try:
                requestedValue = long(parameters["requestedValue"])
                setValue = media_player.setRateAttribute(requestedValue)
                send_to_android( android_sub_cb, 
                    {
                        "target": "SETRATEATTRIBUTE",
                        "setValue": float(setValue)
                    } 
                )
            except Exception as e:
                print("ERROR: ", e)

        if target == "GETVOLUMEATTRIBUTE":
            print("INFO: GETVOLUMEATTRIBUTE INVOKED")
            try:
                value = media_player.getVolumeAttribute()
                send_to_android( android_sub_cb, 
                    {
                        "target": "GETVOLUMEATTRIBUTE",
                        "value": float(value)
                    } 
                )
            except Exception as e:
                print("ERROR: ", e)

        if target == "SETVOLUMEATTRIBUTE":
            requestedValue = float(parameters["requestedValue"])
            print("INFO: SETVOLUMEATTRIBUTE INVOKED: %d" % requestedValue)
            try:
                setValue = media_player.setVolumeAttribute(requestedValue)
                send_to_android( android_sub_cb, 
                    {
                        "target": "SETVOLUMEATTRIBUTE",
                        "setValue": float(setValue)
                    } 
                )
            except Exception as e:
                print("ERROR: ", e)

        if target == "GETCANGONEXTATTRIBUTE":
            print("INFO: GETCANGONEXTATTRIBUTE INVOKED")
            try:
                value = media_player.getCanGoNextAttribute()
                send_to_android( android_sub_cb, 
                    {
                        "target": "GETCANGONEXTATTRIBUTE",
                        "value": bool(value)
                    } 
                )
            except Exception as e:
                print("ERROR: ", e)

        if target == "GETCANGOPREVIOUSATTRIBUTE":
            print("INFO: GETCANGOPREVIOUSATTRIBUTE INVOKED")
            try:
                value = media_player.getCanGoPreviousAttribute()
                send_to_android( android_sub_cb, 
                    {
                        "target": "GETCANGOPREVIOUSATTRIBUTE",
                        "value": bool(value)
                    } 
                )
            except Exception as e:
                print("ERROR: ", e)

        if target == "GETCANPAUSEATTRIBUTE":
            print("INFO: GETCANPAUSEATTRIBUTE INVOKED")
            try:
                value = media_player.getCanPauseAttribute()
                send_to_android( android_sub_cb, 
                    {
                        "target": "GETCANPAUSEATTRIBUTE",
                        "value": bool(value)
                    } 
                )
            except Exception as e:
                print("ERROR: ", e)

        if target == "GETCANPLAYATTRIBUTE":
            print("INFO: GETCANPLAYATTRIBUTE INVOKED")
            try:
                value = media_player.getCanPlayAttribute()
                send_to_android( android_sub_cb, 
                    {
                        "target": "GETCANPLAYATTRIBUTE",
                        "value": bool(value)
                    } 
                )
            except Exception as e:
                print("ERROR: ", e)

        if target == "GETCANSEEKATTRIBUTE":
            print("INFO: GETCANSEEKATTRIBUTE INVOKED")
            try:
                value = media_player.getCanSeekAttribute()
                send_to_android( android_sub_cb, 
                    {
                        "target": "GETCANSEEKATTRIBUTE",
                        "value": bool(value)
                    } 
                )
            except Exception as e:
                print("ERROR: ", e)

        if target == "GETCURRENTTRACKATTRIBUTE":
            print("INFO: GETCURRENTTRACKATTRIBUTE INVOKED")
            try:
                value = media_player.getCurrentTrackAttribute()
                send_to_android( android_sub_cb, 
                    {
                        "target": "GETCURRENTTRACKATTRIBUTE",
                        "value": long(value)
                    } 
                )
            except Exception as e:
                print("ERROR: ", e)

        if target == "GETPLAYBACKSTATUSATTRIBUTE":
            print("INFO: GETPLAYBACKSTATUSATTRIBUTE INVOKED")
            try:
                value = media_player.getPlaybackStatusAttribute()
                send_to_android( android_sub_cb, 
                    {
                        "target": "GETPLAYBACKSTATUSATTRIBUTE",
                        "value": int(value)
                    } 
                )
            except Exception as e:
                print("ERROR: ", e)

        if target == "GETPOSITIONATTRIBUTE":
            print("INFO: GETPOSITIONATTRIBUTE INVOKED")
            try:
                value = media_player.getPositionAttribute()
                send_to_android( android_sub_cb, 
                    {
                        "target": "GETPOSITIONATTRIBUTE",
                        "value": long(value)
                    } 
                )
            except Exception as e:
                print("ERROR: ", e)

        if target == "GETDURATIONATTRIBUTE":
            print("INFO: GETDURATIONATTRIBUTE INVOKED")
            try:
                value = media_player.getDurationAttribute()
                send_to_android( android_sub_cb, 
                    {
                        "target": "GETDURATIONATTRIBUTE",
                        "value": long(value)
                    } 
                )
            except Exception as e:
                print("ERROR: ", e)

        if target == "NEXT":
            print("INFO: NEXT INVOKED")
            try:
                out_e = media_player.next()
                send_to_android( android_sub_cb, 
                    {
                        "target": "NEXT",
                        "_e": int(out_e)
                    } 
                )
            except Exception as e:
                print("ERROR: ", e)

        if target == "OPENURI":
            uri = parameters["uri"]
            print("INFO: OPENURI INVOKED")
            try:
                out_e = media_player.openUri(uri)
                send_to_android( android_sub_cb, 
                    {
                        "target": "OPENURI",
                        "_e": int(out_e)
                    } 
                )
            except Exception as e:
                print("ERROR: ", e)

        if target == "ENQUEUEURI":
            uri = parameters["uri"]
            print("INFO: ENQUEUEURI INVOKED WITH URI %s" % uri)
            try:
                out_e = media_player.enqueueUri(uri)
                send_to_android ( android_sub_cb, 
                    {
                        "target": "ENQUEUEURI",
                        "_e": int(out_e)
                    }
                )
            except Exception as e:
                print("ERROR: ", e)

        if target == "DEQUEUEINDEX":
            pos = parameters["pos"]
            print("INFO: DEQUEUEINDEX INVOKED WITH POS %s" % pos)
            try:
                out_e = media_player.dequeueIndex(dbus.UInt64(pos))
                send_to_android ( android_sub_cb,
                    {
                        "target": "DEQUEUEINDEX",
                        "_e": int(out_e)
                    }
                )
            except Exception as e:
                print("ERROR: ", e)

        if target == "GETCURRENTPLAYQUEUE":
            print("INFO: GETCURRENTPLAYQUEUE INVOKED")
            try:
                # queue has the dbus signature aa{s(yv)}
                # ARRAY of ARRAY of DICT_ENTRY of {STRING, STRUCT of (BYTE, VARIANT)}
                playlist = []
                reply = media_player.getCurrentPlayQueue()
                out_e = reply[1]
                for index, track in enumerate(reply[0]):
                    send_to_android( android_sub_cb, 
                        {
                            "target": "GETCURRENTPLAYQUEUE",
                            "track": Song(track).__dict__,
                            "index": index,
                            "_e": int(out_e)
                        } 
                )
            except Exception as e:
                print("ERROR: ", e)

        if target == "DEQUEUEALL":
            print("INFO: DEQUEUEALL INVOKED")
            try:
                out_e = media_player.dequeueAll()
                send_to_android ( android_sub_cb, 
                    {
                        "target": "DEQUEUEALL",
                        "status": int(out_e)
                    }
                )
            except Exception as e:
                print("ERROR: ", e)

        if target == "OPENPLAYLIST":
            uri = parameters["uri"]
            print("INFO: OPENPLAYLIST INVOKED WITH URI %s" % uri)
            try:
                out_e = media_player.openPlaylist(uri)
                send_to_android ( android_sub_cb, 
                    {
                        "target": "OPENPLAYLIST",
                        "_e": int(out_e)
                    }
                )
            except Exception as e:
                print("ERROR: ", e)

        if target == "PAUSE":
            print("INFO: PAUSE INVOKED")
            try:
                out_e = media_player.pause()
                send_to_android( android_sub_cb, 
                    {
                        "target": "PAUSE",
                        "_e": int(out_e)
                    } 
                )
            except Exception as e:
                print("ERROR: ", e)

        if target == "PLAY":
            print("INFO: PLAY INVOKED")
            try:
                out_e = media_player.play()
                send_to_android( android_sub_cb, 
                    {
                        "target": "PLAY",
                        "_e": int(out_e)
                    } 
                )
            except Exception as e:
                print("ERROR: ", e)

        if target == "PLAYPAUSE":
            print("INFO: PLAYPAUSE INVOKED")
            try:
                out_e = media_player.playPause()
                send_to_android( android_sub_cb, 
                    {
                        "target": "PLAYPAUSE",
                        "_e": int(out_e)
                    } 
                )
            except Exception as e:
                print("ERROR: ", e)

        if target == "PREVIOUS":
            print("INFO: PREVIOUS INVOKED")
            try:
                out_e = media_player.previous()
                send_to_android( android_sub_cb, 
                    {
                        "target": "PREVIOUS",
                        "_e": int(out_e)
                    } 
                )
            except Exception as e:
                print("ERROR: ", e)

        if target == "SEEK":
            pos = long(parameters["pos"])
            print("INFO: SEEK INVOKED WITH POS %d" % pos)
            try:
                out_e = media_player.seek(pos)
                send_to_android( android_sub_cb, 
                    {
                        "target": "SEEK",
                        "_e": int(out_e)
                    } 
                )
            except Exception as e:
                print("ERROR: ", e)

        if target == "SETPOSITION":
            pos = long(parameters["pos"])
            print("INFO: SETPOSITION INVOKED: %d" % pos)
            try:
                out_e = media_player.setPosition(pos)
                send_to_android( android_sub_cb, 
                    {
                        "target": "SETPOSITION",
                        "_e": int(out_e)
                    } 
                )
            except Exception as e:
                print("ERROR: ", e)

        if target == "DISCOVERMM":
            print("INFO: DISCOVERMM INVOKED")
            try:
                reply = media_browser.discoverMediaManagers()
                identifiers = ", ".join(str(x) for x in reply[0])
                out_e = reply[1]
                send_to_android ( android_sub_cb, 
                    {
                        "target": "DISCOVERMM",
                        "identifiers": identifiers,
                        "_e": int(out_e)
                    }
                )
            except Exception as e:
                print("ERROR: ", e)

        if target == "LISTCHILDREN":
            path = parameters["path"]
            try:
                count = parameters["childCount"]
            except KeyError:
                count = 100
            print("INFO: LISTCHILDREN INVOKED")
            media_browser.listChildren(path,
                    0,
                    count,
                    ["*"],
                    reply_handler = children_handler,
                    error_handler = error_handler)

    except Exception as e:
        print("ERROR: ", e)

def children_handler(children, out_e):
    for child in children:
        if child['Type'][1] == 'container':
            thing = MediaContainer(child)
        elif child['Type'][1] == 'music':
            thing = Song(child) 
        else:
            return
        try:
            send_to_android ( android_sub_cb, 
                {
                    "target": "LISTCHILDREN",
                    "children": thing.__dict__,
                    "_e": int(out_e)
                })
        except Exception as e:
            print("ERROR: %s" % e)

def error_handler(err):
    print("LISTCHILDREN ERROR: ", err)
    traceback.print_exc()

def vehiclecontrol_handler(parameters):
    print("INFO: vehiclecontrol invoked")

    try:
        target = parameters["target"]

        if target == "GETLOCATION":
            print("INFO: GETLOCATION INVOKED")
            send_to_android( android_sub_cb,
                {
                                    "target" : "GETLOCATION",
                                    "vehicle_id" : "RangeRover",
                                    "lat" : random.uniform(-90,90),
                                    "lon" : random.uniform(-180,180),
                                    "bearing" : 0
                }
            )
        else:
            print("ERROR: Unknown Vehicle Control Target. Payload was: ", parameters)

    except Exception as e:
        print("ERROR: ", e)

def callback_handler(parameters):
    print("INFO: callback invoked")
    try:
        target = parameters["target"]

        if target == "SMARTHOMESCENARIO":

            if parameters["text"] == "Coming Home":
                print("RECV: From - " + target + " result - " + parameters["result"] )
            elif parameters["text"] == "SmartHome Status":
                print("RECV: From - " + target + " result - " + parameters["result"]  + str(parameters["status"]))
            else:
                print("ERROR: Unknown text string for SMARTHOMESCENARIO")

        else:
            print("ERROR: Unknown Vehicle Control Target. Payload was: ", parameters)
    except Exception as e:
        print("ERROR: ", e)


class SignalThread(threading.Thread):
    def __init__(self, exceptions_queue, bus):
        threading.Thread.__init__(self)
        self.exceptions = exceptions_queue
        self.bus = bus

    def catchall_handler(self, changedValue, **kwargs):
        try:
            send_to_android( android_sub_cb,
                {
                    "signalName": kwargs['member'],
                    "value": massage_type([None, changedValue])
                }
            )
        except Exception as e:
            self.exceptions.put((None, None, kwargs))
            self.exceptions.put(sys.exc_info())

    def run(self):
        try:
            main_loop = gobject.MainLoop()
            self.bus.add_signal_receiver(self.catchall_handler, dbus_interface="org.genivi.mediamanager.Player", member_keyword='member')
            gobject.threads_init()
            main_loop.run()
        except Exception as err:
            main_loop.quit()
            self.exceptions.put(sys.exc_info())


def rvi_runner():

    while True:
        if rvi_client.run_forever(sslopt={"check_hostname": False, "cert_reqs": ssl.CERT_NONE}) is None:
            print("ERROR: NO RVI. Wait and retry...")
            time.sleep(2)
            continue


def gps_ticker():
    time.sleep(2)
    while True:
        send_to_android( android_sub_cb,
            {
                                "target" : "LOCATIONSTATUS",
                                "vehicle_id" : "RangeRover",
                                "lat" : random.uniform(-90,90),
                                "lon" : random.uniform(-180,180),
                                "bearing" : 0
            }
        )

        time.sleep(GPS_TICK_RATE)

if __name__ == "__main__":
    to_reg = {}
    to_reg["vehiclecontrol"] = vehiclecontrol_handler
    to_reg["callback"] = callback_handler
    to_reg["media/mediacontrol"] = mediacontrol_handler

    rvi_client = rvi_ws.rvi_ws_client(bundle_id = "rvi", host = host, services = to_reg, debug = False)

    if GPS_ENABLE:
        print("INFO: FAKE GPS IS ENABLED")
        gps_thread = threading.Thread(target=gps_ticker, args=())
        gps_thread.start()

    else:
        print("INFO: FAKE GPS IS DISABLED")

    exception_queue = Queue.Queue()
    signal_catcher = SignalThread(exception_queue, bus)
    signal_catcher.start()

    try:
        t1 = threading.Thread(target=rvi_runner, args=())
        t1.start()
        while True:
            try:
                exc = exception_queue.get(block=False)
            except Queue.Empty:
                pass
            else:
                exc_type, exc_obj, exc_trace = exc
                print("Signal Catcher exceptions:::\n") 
                print("TYPE:{} ++ OBJ:{} ++ TRACE:{}".format(exc_type, exc_obj, exc_trace))

            signal_catcher.join(0.1)

            if signal_catcher.isAlive():
                continue
            else:
                print("SignalCatcher thread is dead")
                break

    except Exception as err:
        print("\nExiting because :: {}".format(err))

