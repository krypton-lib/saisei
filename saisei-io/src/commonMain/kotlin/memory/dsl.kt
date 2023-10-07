package saisei.io.memory

public operator fun <Item> Memory<Item>.set(idx: Int, value: Item) {
    store(idx.toLong(), value)
}

public operator fun <Item> Memory<Item>.set(idx: Long, value: Item) {
    store(idx, value)
}

public operator fun <Item> Memory<Item>.get(idx: Int): Item = load(idx.toLong())

public operator fun <Item> Memory<Item>.get(idx: Long): Item = load(idx)
