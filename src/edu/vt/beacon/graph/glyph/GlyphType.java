package edu.vt.beacon.graph.glyph;

import edu.vt.beacon.graph.glyph.arc.EquivalenceArc;
import edu.vt.beacon.graph.glyph.arc.LogicArc;
import edu.vt.beacon.graph.glyph.arc.NecessaryStimulation;
import edu.vt.beacon.graph.glyph.arc.NegativeInfluence;
import edu.vt.beacon.graph.glyph.arc.PositiveInfluence;
import edu.vt.beacon.graph.glyph.arc.UnknownInfluence;
import edu.vt.beacon.graph.glyph.node.annotation.Annotation;
import edu.vt.beacon.graph.glyph.node.activity.BiologicalActivity;
import edu.vt.beacon.graph.glyph.node.activity.Phenotype;
import edu.vt.beacon.graph.glyph.node.auxiliary.*;
import edu.vt.beacon.graph.glyph.node.container.Compartment;
import edu.vt.beacon.graph.glyph.node.operator.And;
import edu.vt.beacon.graph.glyph.node.operator.Delay;
import edu.vt.beacon.graph.glyph.node.operator.Not;
import edu.vt.beacon.graph.glyph.node.operator.Or;
import edu.vt.beacon.graph.glyph.node.submap.Submap;
import edu.vt.beacon.graph.glyph.node.submap.Tag;
import edu.vt.beacon.graph.glyph.node.submap.Terminal;

public enum GlyphType
{
    AND                        ("and"),
    ANNOTATION                 ("annotation"),
    BIOLOGICAL_ACTIVITY        ("biological activity"),
    COMPARTMENT                ("compartment"),
    COMPARTMENT_UNIT           ("unit of information"),
    COMPLEX_UNIT               ("complex"),
    DELAY                      ("delay"),
    EQUIVALENCE_ARC            ("equivalence arc"),
    LOGIC_ARC                  ("logic arc"),
    MACROMOLECULE_UNIT         ("macromolecule"),
    NECESSARY_STIMULATION      ("necessary stimulation"),
    NEGATIVE_INFLUENCE         ("negative influence"),
    NUCLEIC_ACID_FEATURE_UNIT  ("nucleic acid feature"),
    NOT                        ("not"),
    OR                         ("or"),
    PERTURBATION_UNIT          ("perturbation"),
    PHENOTYPE                  ("phenotype"),
    POSITIVE_INFLUENCE         ("positive influence"),
    SIMPLE_CHEMICAL_UNIT       ("simple chemical"),
    SUBMAP                     ("submap"),
    TAG                        ("tag"),
    TERMINAL                   ("terminal"),
    UNKNOWN_INFLUENCE          ("unknown influence"),
    UNSPECIFIED_ENTITY_UNIT    ("unspecified entity");


    private String typeString_;

    // TODO document constructor
    private GlyphType(String typeString)
    {
        typeString_ = typeString;
    }

    // FIXME complete method
    public AbstractGlyph newGlyph()
    {
        switch (this) {

            case AND :
                return new And();

            case DELAY :
                return new Delay();

            case EQUIVALENCE_ARC :
                return new EquivalenceArc();

            case LOGIC_ARC :
                return new LogicArc();

            case NECESSARY_STIMULATION :
                return new NecessaryStimulation();

            case NEGATIVE_INFLUENCE :
                return new NegativeInfluence();

            case NOT :
                return new Not();

            case OR :
                return new Or();

            case PHENOTYPE :
                return new Phenotype();

            case BIOLOGICAL_ACTIVITY:
                return new BiologicalActivity();

            case POSITIVE_INFLUENCE :
                return new PositiveInfluence();

            case COMPARTMENT:
                return new Compartment();

            case SUBMAP :
                return new Submap();

            case UNKNOWN_INFLUENCE :
                return new UnknownInfluence();

            case COMPLEX_UNIT :
                return new ComplexUnit();

            case MACROMOLECULE_UNIT :
                return new MacromoleculeUnit();

            case NUCLEIC_ACID_FEATURE_UNIT :
                return new NucleicAcidFeatureUnit();

            case PERTURBATION_UNIT :
                return new PerturbationUnit();

            case SIMPLE_CHEMICAL_UNIT :
                return new SimpleChemicalUnit();

            case UNSPECIFIED_ENTITY_UNIT :
                return new UnspecifiedEntityUnit();

            case COMPARTMENT_UNIT:
                return new CompartmentUnit();

            case ANNOTATION:
                return new Annotation();

            case TAG:
                return new Tag(null);

            case TERMINAL:
                return new Terminal(null);
        }

        throw new IllegalStateException("missing glyph type case");
    }

    // TODO document method
    @Override
    public String toString()
    {
        return typeString_;
    }
}