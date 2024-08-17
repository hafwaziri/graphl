import subprocess
import os
import pytest

input_file = "input_src.txt"
output_file = "output.txt"
input_path = "./Test/FullstackTests/Inputs"


@pytest.mark.parametrize("fileName", filter(lambda x: x.endswith("txt"), [i for _, _, files in os.walk(input_path) for i in files]))
def test_file(fileName: str, nocompile) -> None:

	filePath = "Test/FullstackTests/Inputs/" + fileName

	command = f"./run.sh " + (" " if nocompile else "-r ") + filePath + " > output.txt"

	subprocess.run(command, shell=True, executable="/bin/bash")
	with open("./Test/FullstackTests/Outputs/"+fileName, "r") as expected, open(output_file, "r") as received:
		assert received.read() == expected.read()

