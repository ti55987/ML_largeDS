from guineapig import *
import math
import itertools
from operator import itemgetter
# supporting routines can go here
#def tokens(line):


def labelSum(line):
	docid = line[0]
	event = line[1]
	event = sorted(event,key=itemgetter(0))
	label = event[0][0]
	count = 0.0
	for e in event:
		curr = e[0]
		if curr == label:
			count = count + e[1]
		else:
			yield(docid, label, count)
			label = curr
			count = e[1]
	yield(docid, label, count)

#always subclass Planner
class NB(Planner):
	# params is a dictionary of params given on the command line. 
	# e.g. trainFile = params['trainFile']
	params = GPig.getArgvParams()
	trainFile = params['trainFile']
	testFile = params['testFile']

	idDoc = ReadLines(trainFile) | Map(by=lambda line:line.strip().split("\t"))\
	| Map(by=lambda (docid,label,doc):(label.split(','),doc.lower().split()))
	#(dom(V))
	V = Group(idDoc, by=lambda (label,doc):'V', retaining=lambda (label,doc):len(doc), reducingTo=ReduceToSum()) \
	| Map(by=lambda (V,val):float(1)/val)

	Pr_w = FlatMap(idDoc, by=lambda (label,doc): map(lambda l:(l, doc), label))\
	| Group(by=lambda (label,word):label, retaining=lambda (label,doc):doc, reducingTo=ReduceToList())\
	| Map(by=lambda (docid, doc):(docid, list(itertools.chain.from_iterable(doc)), 1+len(list(itertools.chain.from_iterable(doc)))))\
	| FlatMap(by=lambda (label,doc, count): map(lambda w:(label,count, w), doc))\
	| Group(by=lambda (label,ToAny,word):(label,ToAny,word), reducingTo=ReduceToCount())\
	| Augment(sideview=V,loadedBy=lambda v:GPig.onlyRowOf(v))\
	| Map(by=lambda (((label,ToAny,word), count),V):(label, word, ToAny, count+V))\
	| Map(by=lambda ((label, word, ToAny, count)):(label, word, math.log(float(count)/ToAny)))\
	| Group(by=lambda (label, word, count):word, retaining=lambda (label, word, count):(label, count), \
		reducingTo=ReduceToList()) 

	#C(Y=y)
	LabelToID = FlatMap(idDoc, by=lambda (label,doc): map(lambda l:(l, len(doc)),label)) \
	| Group(by=lambda (label,count):label, reducingTo=ReduceToCount())
	#C(Y=*)
	TrainingNum = Group(LabelToID, by=lambda (label,count):'ANY',retaining=lambda (label,count):count, \
	reducingTo=ReduceToSum()) | Map(by=lambda (ANY,val):float(val+1))
	#(dom(Y))
	DomY = Group(LabelToID, by=lambda (label,count):'DomY', reducingTo=ReduceToCount()) | Map(by=lambda (ANY,val):float(1)/val)
	#Pr(Y=y)
	Pr_y = Augment(LabelToID, sideview=DomY,loadedBy=lambda v:GPig.onlyRowOf(v)) \
	| Augment(sideview=TrainingNum,loadedBy=lambda v:GPig.onlyRowOf(v)) \
	| Map(by=lambda ((((label,count),Y),val)):(label, math.log(float(count+Y)/val)))

	#Testing Part
	test = ReadLines(testFile) | Map(by=lambda line:line.strip().split("\t"))\
	| Map(by=lambda (docid,label,doc): (docid,doc.lower().split()))\
	| FlatMap( by=lambda (docid,words): map(lambda w:(w,docid),words))\
	| JoinTo( Jin(Pr_w,by=lambda (word, event):word), by=lambda (word,docid):word)\
	| Group(by=lambda ((test,docid),(train, event)):docid, retaining=lambda ((test,docid),(train, event)):event,\
	reducingTo=ReduceToList()) | Map(by=lambda (docid, event):(docid, list(itertools.chain.from_iterable(event))))\
	| Flatten(by=labelSum)

	prediction = Join( Jin(test, by=lambda (docid, label,count):label), \
		Jin(Pr_y,by=lambda (label,count):label))\
	| Map(by=lambda ((docid, label,test),(labels,train)):(docid, label, test+train))\
	| Group(by=lambda (docid, label, prob):docid, reducingTo=ReduceToList())
	output = Map(prediction, by=lambda (docid, result):min(result,key=lambda item:item[2]))
# always end like this
if __name__ == "__main__":
    NB().main(sys.argv)

# supporting routines can go here
