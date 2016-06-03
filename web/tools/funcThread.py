import threading


class FuncThread(threading.Thread):

	def __init__(self, target, args):
		self._target =target
		self._args =args
		threading.Thread.__init__(self)
		self._stop = threading.Event()
		self.daemon = True
 
	def run(self):
		self._target(*self._args)

	def stop(self):
		self._stop.set()

	def stopped(self):
		return self._stop.isSet()


# Test
if __name__ == '__main__':
	import time

	def someOtherFunc(data, key):
		while True:
			#time.sleep(1)
			print data, key


	t1 = FuncThread(target=someOtherFunc, args=([10,2], 6))
	t1.start()


	time.sleep(2)
	t1.stop()
	print "stopped"