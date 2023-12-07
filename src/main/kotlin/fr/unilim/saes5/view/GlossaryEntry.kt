package fr.unilim.saes5.view

class GlossaryEntry(
    val mot: String,
    val definition: String,
    val primaryContext: String,
    val secondaryContext: String,
    val synonym: String,
    val antonym: String
) {
    override fun toString(): String {
        return "GlossaryEntry(mot='$mot', definition='$definition', primaryContext='$primaryContext', secondaryContext='$secondaryContext', synonym='$synonym', antonym='$antonym')"
    }
}