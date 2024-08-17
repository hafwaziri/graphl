
recompile(){
    find . -name "*.java" > sources.txt
    javac -d out -cp ".:lib/jackson/*:lib/junit/*" @sources.txt
    rm sources.txt
}

printhelp(){
  printf "usage: run.sh [options] source\n\n"
  printf "positional arguments:\n"
  printf " source\t- path to the file containing your sourcecode\n\n"
  printf "options:\n"
  printf " -h\tshow this helptext and exit\n"
  printf " -r\trecompile the java code\n"
  printf " -d\tshow debug output\n"
}

debugoutput=false
while getopts drhf flag
do
    case "${flag}" in
        d) debugoutput=true
          ;;
        r) recompile
          ;;
        h) printhelp
          exit 1
          ;;
        ?) echo "Invalid option ${flag}"
          printhelp
          ;;
    esac

done

source=${@:$OPTIND:1}

if [[ -z "$source" ]]; then
  printhelp
  exit 1
fi

check_files() {
    for file in "${required_files[@]}"; do
        if [ ! -f "$file" ]; then
            return 1
        fi
    done
    return 0
}

if [[ "${debugoutput}" == false ]]; then
  python3 ./parser/parser.py "../${source}" | java -cp "out:lib/jackson/*:lib/junit/*" Tools.JSONParser
else
  python3 ./parser/parser.py -db -o "../ast.json" "../${source}"
  less "ast.json" | java -cp "out:lib/jackson/*:lib/junit/*" Tools.JSONParser -o && rm "ast.json"
fi
