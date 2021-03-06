package utils

import scala.util.Random

object NameGenerator {
	val adjectives = Vector(
		"adorable",
		"alert",
		"average",
		"bloody",
		"blushing",
		"bright",
		"clean",
		"clear",
		"cloudy",
		"colorful",
		"crowded",
		"cute",
		"dark",
		"drab",
		"distinct",
		"dull",
		"elegant",
		"excited",
		"fancy",
		"filthy",
		"gleaming",
		"gorgeous",
		"graceful",
		"handsome",
		"homely",
		"light",
		"long",
		"misty",
		"muddy",
		"plain",
		"poised",
		"precious",
		"quaint",
		"shiny",
		"smoggy",
		"spotless",
		"stormy",
		"strange",
		"ugly",
		"ugliest",
		"unusual",
		"alive",
		"annoying",
		"bad",
		"better",
		"brainy",
		"busy",
		"careful",
		"cautious",
		"clever",
		"clumsy",
		"crazy",
		"curious",
		"dead",
		"doubtful",
		"easy",
		"famous",
		"fragile",
		"frail",
		"gifted",
		"helpful",
		"helpless",
		"horrible",
		"innocent",
		"modern",
		"mushy",
		"odd",
		"open",
		"poor",
		"powerful",
		"prickly",
		"puzzled",
		"real",
		"rich",
		"shy",
		"sleepy",
		"stupid",
		"super",
		"talented",
		"tame",
		"tender",
		"tough",
		"vast",
		"wild",
		"wrong",
		"amused",
		"brave",
		"calm",
		"charming",
		"cheerful",
		"eager",
		"elated",
		"excited",
		"fair",
		"faithful",
		"fine",
		"friendly",
		"funny",
		"gentle",
		"glorious",
		"good",
		"happy",
		"healthy",
		"helpful",
		"jolly",
		"joyous",
		"kind",
		"lively",
		"lovely",
		"lucky",
		"nice",
		"obedient",
		"perfect",
		"pleasant",
		"proud",
		"relieved",
		"silly",
		"smiling",
		"splendid",
		"thankful",
		"witty",
		"zealous",
		"zany"
	)

	val animals = Vector(
		"alpaca",
		"ant",
		"anteater",
		"antelope",
		"ape",
		"herd",
		"baboon",
		"badger",
		"bat",
		"bear",
		"beaver",
		"bee",
		"bison",
		"boar",
		"galago",
		"camel",
		"caribou",
		"cat",
		"cattle",
		"chamois",
		"cheetah",
		"chicken",
		"chough",
		"clam",
		"cobra",
		"cod",
		"coyote",
		"crab",
		"herd",
		"crow",
		"curlew",
		"deer",
		"dinosaur",
		"dog",
		"dolphin",
		"donkey",
		"dotterel",
		"dove",
		"duck",
		"dugong",
		"dunlin",
		"eagle",
		"echidna",
		"eel",
		"elephant",
		"elk",
		"emu",
		"falcon",
		"ferret",
		"finch",
		"fish",
		"flamingo",
		"fly",
		"fox",
		"frog",
		"gaur",
		"gazelle",
		"gerbil",
		"giraffe",
		"gnat",
		"goat",
		"goose",
		"goldfish",
		"gorilla",
		"goshawk",
		"grouse",
		"guanaco",
		"poultry",
		"herd",
		"gull",
		"hamster",
		"hare",
		"hawk",
		"hedgehog",
		"heron",
		"herring",
		"hornet",
		"horse",
		"human",
		"hyena",
		"jackal",
		"jaguar",
		"jay",
		"kangaroo",
		"koala",
		"kouprey",
		"kudu",
		"lapwing",
		"lark",
		"lemur",
		"leopard",
		"lion",
		"llama",
		"lobster",
		"locust",
		"loris",
		"louse",
		"lyrebird",
		"magpie",
		"mallard",
		"manatee",
		"marten",
		"meerkat",
		"mink",
		"monkey",
		"moose",
		"mouse",
		"mosquito",
		"mule",
		"narwhal",
		"newt",
		"octopus",
		"okapi",
		"opossum",
		"oryx",
		"ostrich",
		"otter",
		"owl",
		"ox",
		"oyster",
		"parrot",
		"peafowl",
		"pelican",
		"penguin",
		"pheasant",
		"pig",
		"pigeon",
		"pony",
		"porpoise",
		"quail",
		"quelea",
		"rabbit",
		"raccoon",
		"rat",
		"raven",
		"herd",
		"reindeer",
		"ruff",
		"salmon",
		"sardine",
		"scorpion",
		"herd",
		"seahorse",
		"shark",
		"sheep",
		"shrew",
		"shrimp",
		"skunk",
		"snail",
		"snake",
		"spider",
		"squid",
		"squirrel",
		"starling",
		"stingray",
		"stinkbug",
		"stork",
		"swallow",
		"swan",
		"tapir",
		"tarsier",
		"termite",
		"tiger",
		"toad",
		"trout",
		"poultry",
		"turtle",
		"vulture",
		"wallaby",
		"walrus",
		"wasp",
		"weasel",
		"whale",
		"wolf",
		"wombat",
		"worm",
		"yak",
		"zebra"
	)

	def random(collection: Vector[String]): String = collection(Random.nextInt(collection.size))

	def randomAdjective: String = random(adjectives).capitalize
	def randomAnimal: String = random(animals).capitalize
	def generate: String = randomAdjective + randomAnimal
}
