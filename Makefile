.PHONY: all emu clean-all run-emu

OBJ_DIR := output
SRC_DIR := src
SCALA_FILES := $(shell find $(SRC_DIR) -name "*.scala")
V_FILES := $(shell find $(SRC_DIR) -name "*.v")
SV_FILES := $(shell find $(SRC_DIR) -name "*.sv")

EMU_SRC_DIR := emu
EMU_TOP_MODULE := Top
EMU_TOP_V := $(OBJ_DIR)/emu_top.v
EMU_MK := $(OBJ_DIR)/emu.mk
EMU_BIN := $(OBJ_DIR)/emulator
EMU_CXXFILES := $(shell find $(EMU_SRC_DIR) -name "*.cpp")

emu: $(EMU_BIN)
run-emu: emu
	@$(EMU_BIN)

$(EMU_TOP_V): $(SCALA_FILES)
	@mkdir -p $(@D)
	@sbt "run MainDriver -X verilog -td $(@D) -o $(notdir $@)"

$(EMU_MK): $(EMU_TOP_V) $(EMU_CXXFILES) $(V_FILES) $(SV_FILES)
	@verilator --cc --exe --top-module $(EMU_TOP_MODULE) \
	  -o $(notdir $(EMU_BIN)) -Mdir $(@D) \
	  --prefix $(basename $(notdir $(EMU_MK))) $^ -CFLAGS "-g"

$(EMU_BIN): $(EMU_MK) $(EMU_CXXFILES)
	@cd $(@D) && make -s -f $(notdir $<)

clean-all:
	rm -rf $(OBJ_DIR)
